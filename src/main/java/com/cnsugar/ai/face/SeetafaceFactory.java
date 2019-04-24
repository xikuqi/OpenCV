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
 * @Copyright 上海云辰信息科技有限公司
 */
public class SeetafaceFactory {
    private static Logger logger = LoggerFactory.getLogger(SeetafaceFactory.class);
    private static SeetaFace2JNI seeta = null;

    public volatile static Boolean face_db_init = false;

    static {
        Properties prop = getConfig();
        System.setProperty("java.library.path", prop.getProperty("libs.path", ""));
        try {//使java.library.path生效
            Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        logger.debug("java.library.path: {}", System.getProperty("java.library.path"));
        String[] libs = prop.getProperty("libs", "").split(",");
        for (String lib : libs) {
            logger.debug("load library: {}", lib);
            System.loadLibrary(lib);
        }
        String bindata = prop.getProperty("bindata.dir");
        logger.debug("bindata dir: {}", bindata);
        seeta = new SeetaFace2JNI();
        seeta.initModel(bindata);

        new Thread(() -> loadFaceDb(prop)).start();
        logger.info("Seetaface init completed!!!");
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

    public static SeetaFace2JNI getSeetaJNI() {
        return seeta;
    }

    /**
     * 加载人脸库
     */
    public synchronized static void loadFaceDb(Properties prop) {
        if (face_db_init) {
            return;
        }
        String db_file = prop.getProperty("sqlite.db.file");
        if (db_file == null) {
            logger.warn("没有配置sqlite.db.file，人脸注册(register)及人脸搜索(1 v N)功能将无法使用!!!");
            return;
        }
        logger.info("load face data...");
        System.setProperty("seetaface.db", db_file);
        System.setProperty(JdbcPool.MAX_TOTAL, prop.getProperty(JdbcPool.MAX_TOTAL));
        System.setProperty(JdbcPool.MAX_IDLE, prop.getProperty(JdbcPool.MAX_IDLE));
        System.setProperty(JdbcPool.MIN_IDLE, prop.getProperty(JdbcPool.MIN_IDLE));
        System.setProperty(JdbcPool.MAX_WAIT_MILLIS, prop.getProperty(JdbcPool.MAX_WAIT_MILLIS));
        int pageNo = 0, pageSize = 100;
//        seeta.clear();
        FaceDao.clearIndex();
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
        face_db_init = true;
    }

    /**
     * 将历史注册过的所有人脸重新加载到内存库中
     *
     * @param key  人脸照片唯一标识
     * @param face 人脸照片
     * @return
     * @throws IOException
     */
    public static void register(String key, FaceIndex face) {
        SeetaImageData imageData = new SeetaImageData(256, 256, 3);
        imageData.data = face.getImgData();
        int index = getSeetaJNI().register(imageData);
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
}
