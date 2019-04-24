import com.cnsugar.ai.face.FaceHelper;
import com.cnsugar.ai.face.SeetafaceFactory;
import com.cnsugar.ai.face.bean.Result;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Test {

    @org.junit.Test
    public void testCompare() throws Exception {
        String img1 = "F:\\ai\\demo-pic39.jpg";
        String img2 = "F:\\ai\\left_pic_one.jpg";
        System.out.println("result:" + FaceHelper.compare(new File(img1), new File(img2)));
    }

    @org.junit.Test
    public void testRegister() throws IOException {
        //将F:\ai\star目录下的jpg、png图片都注册到人脸库中，以文件名为key
        Collection<File> files = FileUtils.listFiles(new File("F:\\ai\\star"), new String[]{"jpg", "png"}, false);
        for (File file : files) {
            String key = file.getName();
            try {
                FaceHelper.register(key, FileUtils.readFileToByteArray(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @org.junit.Test
    public void testSearch() throws IOException {
        while (!SeetafaceFactory.face_db_init) {
            //等待初始完成
        }
        long l = System.currentTimeMillis();
        Result result = FaceHelper.search(FileUtils.readFileToByteArray(new File("F:\\ai\\gtl.jpg")));
        System.out.println("搜索结果：" + result + "， 耗时：" + (System.currentTimeMillis() - l));
    }

    @org.junit.Test
    public void testCorp() throws IOException {
        BufferedImage image = FaceHelper.crop(FileUtils.readFileToByteArray(new File("F:\\ai\\jobs0.jpg")));
        ImageIO.write(image, "jpg", new File("F:\\ai\\corp-face.jpg"));
    }
}
