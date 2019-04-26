# seetafaceJNI

#### 项目介绍
基于中科院seetaface2进行封装的JAVA人脸识别算法库，支持人脸识别、1:1比对、1:N比对。
seetaface2：https://github.com/seetaface/SeetaFaceEngine2

#### 环境配置
1、下载model（ https://pan.baidu.com/s/1HJj8PEnv3SOu6ZxVpAHPXg ） 文件到本地，并解压出来；

2、下载doc目录中对应的lib包到本地并解压：Windows(64位)环境下载lib-win-x64.zip、Linux(64位)下载lib-linux-x64.tar.bz2，Linux环境还需要安装依赖库，详见：https://my.oschina.net/u/1580184/blog/3042404 ；

3、将doc中的faces-data.db下载到本地；（PS：如果不需要使用1:N人脸搜索,不需要此文件，需要将seetafce.properties中的sqlite.db.file配置注释掉）；

4、将src/main/resources/中的seetaface.properties文件放到项目的resources根目录中；

```properties
#linux系统中依赖的lib名称
libs=holiday,SeetaFaceDetector200,SeetaPointDetector200,SeetaFaceRecognizer200,SeetaFaceCropper200,SeetaFace2JNI
#Windows系统中依赖的lib名称
#libs=libgcc_s_sjlj-1,libeay32,libquadmath-0,ssleay32,libgfortran-3,libopenblas,holiday,SeetaFaceDetector200,SeetaPointDetector200,SeetaFaceRecognizer200,SeetaFaceCropper200,SeetaFace2JNI

#lib存放目录
libs.path=/usr/local/seetaface2/lib
#model存放目录
bindata.dir=/usr/local/seetaface2/bindata

##sqlite配置(如果不用1:N人脸搜索功能，请删除下面5项sqlite开头的配置)
sqlite.db.file=/data/faces-data.db
sqlite.conn.maxTotal=50
sqlite.conn.maxIdle=5
sqlite.conn.minIdle=0
sqlite.conn.maxWaitMillis=60000
```


5、将seetaface-1.0.jar和依赖包导入到项目中，pom如下:

```xml
   <properties>
       <spring.version>4.2.8.RELEASE</spring.version>
       <log4j.version>2.8.2</log4j.version>
       <slf4j.version>1.7.25</slf4j.version>
   </properties>
  
   <dependencies>
       <dependency>
            <groupId>com.cnsugar.ai</groupId>
            <artifactId>seetafaceJNI</artifactId>
            <version>1.0</version>
            <!--<scope>system</scope>-->
            <!--<systemPath>${project.basedir}/lib/seetafaceJNI-1.0.jar</systemPath>-->
       </dependency>
       <dependency>
           <groupId>org.springframework</groupId>
           <artifactId>spring-core</artifactId>
           <version>${spring.version}</version>
       </dependency>
  
       <dependency>
           <groupId>org.slf4j</groupId>
           <artifactId>slf4j-api</artifactId>
           <version>${slf4j.version}</version>
       </dependency>
  
       <!-- sqlite -->
       <dependency>
           <groupId>org.xerial</groupId>
           <artifactId>sqlite-jdbc</artifactId>
           <version>3.25.2</version>
       </dependency>
       <dependency>
           <groupId>org.apache.commons</groupId>
           <artifactId>commons-pool2</artifactId>
           <version>2.4.2</version>
       </dependency>
   </dependencies> 
```

6、调用FaceHelper中的方法。


#### 使用方法
所有方法都封装到了FaceHelper工具类中
```java
    /**
     * 人脸比对
     *
     * @param img1
     * @param img2
     * @return 相似度
     */
    float compare(File img1, File img2);
    float compare(byte[] img1, byte[] img2);
    float compare(BufferedImage image1, BufferedImage image2);
    
    /**
     * 注册人脸（会裁剪图片）
     *
     * @param key 人脸照片唯一标识
     * @param img 人脸照片
     * @return 
     */
    boolean register(String key, byte[] img);
    /**
     * 注册人脸（不裁剪图片）
     *
     * @param key 人脸照片唯一标识
     * @param image 人脸照片
     * @return 
     */
    boolean register(String key, BufferedImage image)
    
    /**
     * 搜索人脸
     *
     * @param img 人脸照片
     * @return
     */
    Result search(byte[] img);
    Result search(BufferedImage image);
    
    /**
     * 人脸提取（裁剪）
     *
     * @param img
     * @return return cropped face
     */
    BufferedImage crop(byte[] img);
    BufferedImage crop(BufferedImage image);
    
    /**
     * 人脸识别
     *
     * @param img
     * @return
     */
    SeetaRect[] detect(byte[] img);
    SeetaRect[] detect(BufferedImage image);

    /**
     * 人脸识别(包含5个特征点位置)
     *
     * @param image
     * @return
     */
    FaceLandmark detectLandmark(BufferedImage image);
    
    /**
     * 删除已注册的人脸
     * @param keys
     */
    void removeRegister(String... keys);    
    
```

- 示例代码：1:1人脸比对
```java
    @org.junit.Test
    public void testCompare() throws Exception {
        String img1 = "F:\\ai\\demo-pic39.jpg";
        String img2 = "F:\\ai\\left_pic_one.jpg";
        System.out.println("result:"+FaceHelper.compare(new File(img1), new File(img2)));
    }
```

- 示例代码：1:N人脸搜索
  先调用FaceHelper.register()方法将人脸图片注册到seetaface2的人脸库(内存)中，同时会将图片存在sqlite数据库中进行持久化，下次应用程序启动时会自动从sqlite中把图片读取出来重新注册到seetafce2的内存库中

```java
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
        while (SeetafaceBuilder.getFacedbStatus() != SeetafaceBuilder.FacedbStatus.OK) {
            //程序启动时要等待历史注册的人脸加载到内存库中
            if (SeetafaceBuilder.getFacedbStatus() == SeetafaceBuilder.FacedbStatus.INACTIV) {
                System.out.println("人脸数据库未配置");
                System.exit(1);
            }
        }
        long l = System.currentTimeMillis();
        Result result = FaceHelper.search(FileUtils.readFileToByteArray(new File("F:\\ai\\gtl.jpg")));
        System.out.println("搜索结果：" + result + "， 耗时：" + (System.currentTimeMillis() - l));
    }
```