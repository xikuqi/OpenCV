package com.seetaface2;

import com.seetaface2.model.*;

/**
 * SeetaFace2JNI接口
 */
public class SeetaFace2JNI {
    /**
     * 初始化，指定人脸识别模型文件目录，该目录下应当包括这3个文件：
     * SeetaFaceDetector2.0.ats,
     * SeetaFaceRecognizer2.0.ats,
     * SeetaPointDetector2.0.pts5.ats
     *
     * @param modelDir
     * @return
     */

    public native boolean initModel(String modelDir);

    /**
     * 检测人脸
     *
     * @param img
     * @return
     */
    public native SeetaRect[] detect(SeetaImageData img);

    /**
     * 特征对齐
     *
     * @param img
     * @param faces
     * @return
     */
    public native SeetaPointF[] detect(SeetaImageData img, SeetaRect[] faces);

    /**
     * 1 v 1 人脸比对
     *
     * @param img1
     * @param img2
     * @return 相似度范围在0~1,返回负数表示出错
     */
    public native float compare(SeetaImageData img1, SeetaImageData img2);

    /**
     * 注册人脸
     *
     * @param img
     * @return The returned value is the index of face database. Reture -1 if failed
     */
    public native int register(SeetaImageData img);

    /**
     * Recognize face and get the most similar face index
     *
     * @param img
     * @return index saves the index of face databese, which is same as the retured value by Register. similar saves the most similar.
     */
    public native RecognizeResult recognize(SeetaImageData img);


    /**
     * Clear face database
     */
    public native void clear();


    /**
     * 人脸提取
     *
     * @param img
     * @return The returned value is face data. Reture null if failed
     */
    public native byte[] crop(SeetaImageData img);
}  