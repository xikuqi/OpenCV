package com.cnsugar.ai.face;

import com.cnsugar.ai.face.bean.FaceIndex;
import com.cnsugar.ai.face.dao.FaceDao;
import com.cnsugar.common.sqlite.JdbcPool;
import com.seetaface2.SeetaFace2JNI;
import com.seetaface2.model.SeetaImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

/**
 * @Author Sugar
 * @Version 2019/4/22 14:28
 */
public class SeetafaceBuilder {
    private static Logger logger = LoggerFactory.getLogger(SeetafaceBuilder.class);
    private volatile static SeetaFace2JNI seeta = null;

    public enum FacedbStatus {
        READY, LOADING, OK, INACTIV;
    }

    private volatile static FacedbStatus face_db_status = FacedbStatus.READY;

    public static SeetaFace2JNI build() {
        if (seeta == null) {
            synchronized (SeetafaceBuilder.class) {
                if (seeta != null) {
                    return seeta;
                }
                init();
            }
        }
        return seeta;
    }

    /**
     * 建立人脸库索引
     */
    public static void buildIndex() {
        synchronized (SeetafaceBuilder.class) {
            while (face_db_status == FacedbStatus.LOADING || face_db_status == FacedbStatus.READY) {
                //等待之前的任务初始化完成
            }
            face_db_status = FacedbStatus.READY;
            new Thread(() -> {
                seeta.clear();
                loadFaceDb();
            }).start();
        }
    }

    private static void init() {
        Properties prop = getConfig();
        String separator = System.getProperty("path.separator");
        String sysLib = System.getProperty("java.library.path");
        if (sysLib.endsWith(separator)) {
            System.setProperty("java.library.path", sysLib + prop.getProperty("libs.path", ""));
        } else {
            System.setProperty("java.library.path", sysLib + separator + prop.getProperty("libs.path", ""));
        }
        try {//使java.library.path生效
            Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String[] libs = prop.getProperty("libs", "").split(",");
        for (String lib : libs) {
            logger.debug("load library: {}", lib);
            System.loadLibrary(lib);
        }
        String bindata = prop.getProperty("bindata.dir");
        logger.debug("bindata dir: {}", bindata);
        seeta = new SeetaFace2JNI();
        seeta.initModel(bindata);
        String db_file = prop.getProperty("sqlite.db.file");
        if (db_file != null) {
            System.setProperty("seetaface.db", db_file);
            System.setProperty(JdbcPool.MAX_TOTAL, prop.getProperty(JdbcPool.MAX_TOTAL));
            System.setProperty(JdbcPool.MAX_IDLE, prop.getProperty(JdbcPool.MAX_IDLE));
            System.setProperty(JdbcPool.MIN_IDLE, prop.getProperty(JdbcPool.MIN_IDLE));
            System.setProperty(JdbcPool.MAX_WAIT_MILLIS, prop.getProperty(JdbcPool.MAX_WAIT_MILLIS));

            new Thread(() -> loadFaceDb()).start();
        } else {
            face_db_status = FacedbStatus.INACTIV;
            logger.warn("没有配置sqlite.db.file，人脸注册(register)及人脸搜索(1 v N)功能将无法使用!!!");
        }
        logger.info("Seetaface init completed!!!");
    }

    /**
     * 加载人脸库
     */
    private static void loadFaceDb() {
        if (face_db_status != FacedbStatus.READY) {
            return;
        }

        if (System.getProperty("seetaface.db") == null) {
            face_db_status = FacedbStatus.INACTIV;
            logger.error("没有配置sqlite.db.file!!!");
            return;
        }

        face_db_status = FacedbStatus.LOADING;
        logger.info("load face data...");
        FaceDao.clearIndex();
        int pageNo = 0, pageSize = 100;
        while (true) {
            List<FaceIndex> list = FaceDao.findFaceImgs(pageNo, pageSize);
            if (list == null) {
                break;
            }
            list.forEach(face -> {
                try {
                    register(face.getKey(), face);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (list.size() < pageSize) {
                break;
            }
            pageNo++;
        }
        face_db_status = FacedbStatus.OK;
    }

    /**
     * 将历史注册过的所有人脸重新加载到内存库中
     *
     * @param key  人脸照片唯一标识
     * @param face 人脸照片
     * @return
     * @throws IOException
     */
    private static void register(String key, FaceIndex face) {
        SeetaImageData imageData = new SeetaImageData(256, 256, 3);
        imageData.data = face.getImgData();
        int index = seeta.register(imageData);
        if (index < 0) {
            logger.info("Register face fail: key={}, index={}", key, index);
            return;
        }
        FaceIndex faceIndex = new FaceIndex();
        faceIndex.setKey(key);
        faceIndex.setIndex(index);
        FaceDao.saveOrUpdateIndex(faceIndex);
        logger.info("Register face success: key={}, index={}", key, index);
    }

    private static Properties getConfig() {
        Properties properties = new Properties();
        String location = "classpath:/seetaface.properties";
        try (InputStream is = new DefaultResourceLoader().getResource(location).getInputStream()) {
            properties.load(is);
            logger.debug("seetaface config: {}", properties.toString());
        } catch (IOException ex) {
            logger.error("Could not load property file:" + location, ex);
        }
        return properties;
    }
}
