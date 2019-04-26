package com.cnsugar.ai.face.utils;

//import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
//import java.awt.image.ColorConvertOp;
import java.awt.image.ComponentSampleModel;
import java.util.Arrays;

/**
 * @Author Sugar
 * @Version 2019/4/4 16:05
 */
public class ImageUtils {
    /**
     * @param image
     * @param bandOffset 用于推断通道顺序
     * @return
     */
    private static boolean equalBandOffsetWith3Byte(BufferedImage image, int[] bandOffset) {
        if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            if (image.getData().getSampleModel() instanceof ComponentSampleModel) {
                ComponentSampleModel sampleModel = (ComponentSampleModel) image.getData().getSampleModel();
                if (Arrays.equals(sampleModel.getBandOffsets(), bandOffset)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 推断图像是否为BGR格式
     *
     * @return
     */
    public static boolean isBGR3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{0, 1, 2});
    }

    /**
     * 对图像解码返回BGR格式矩阵数据
     *
     * @param image
     * @return
     */
    public static byte[] getMatrixBGR(BufferedImage image) {
        byte[] matrixBGR;
        if (isBGR3Byte(image)) {
            matrixBGR = (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        } else {
            // ARGB格式图像数据
            int intrgb[] = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            matrixBGR = new byte[image.getWidth() * image.getHeight() * 3];
            // ARGB转BGR格式
            for (int i = 0, j = 0; i < intrgb.length; ++i, j += 3) {
                matrixBGR[j] = (byte) (intrgb[i] & 0xff);
                matrixBGR[j + 1] = (byte) ((intrgb[i] >> 8) & 0xff);
                matrixBGR[j + 2] = (byte) ((intrgb[i] >> 16) & 0xff);
            }
        }
        return matrixBGR;
    }

    /**
     * 判断图像是否为RGB格式
     *
     * @return
     */
//    public static boolean isRGB3Byte(BufferedImage image) {
//        return equalBandOffsetWith3Byte(image, new int[]{2, 1, 0});
//    }

    /**
     *   * 对图像解码返回RGB格式矩阵数据
     *   * @param image
     *   * @return
     *   
     */
//    public static byte[] getMatrixRGB(BufferedImage image) {
//        byte[] matrixRGB;
//        if (isRGB3Byte(image)) {
//            matrixRGB = (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
//        } else {
//            // 转RGB格式
//            BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(image, rgbImage);
//            matrixRGB = (byte[]) rgbImage.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
//        }
//        return matrixRGB;
//    }

    public static BufferedImage bgrToBufferedImage(byte[] data, int width, int height) {
        int type = BufferedImage.TYPE_3BYTE_BGR;
        // bgr to rgb
        byte b;
        for (int i = 0; i < data.length; i = i + 3) {
            b = data[i];
            data[i] = data[i + 2];
            data[i + 2] = b;
        }
        BufferedImage image = new BufferedImage(width, height, type);
        image.getRaster().setDataElements(0, 0, width, height, data);
        return image;
    }

    /**
     * 裁剪图片方法
     *
     * @param bufferedImage 图像源
     * @param startX        裁剪开始x坐标
     * @param startY        裁剪开始y坐标
     * @param endX          裁剪结束x坐标
     * @param endY          裁剪结束y坐标
     * @return
     */
//    public static BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY) {
//        int width = bufferedImage.getWidth();
//        int height = bufferedImage.getHeight();
//        if (startX == -1) {
//            startX = 0;
//        }
//        if (startY == -1) {
//            startY = 0;
//        }
//        if (endX == -1) {
//            endX = width - 1;
//        }
//        if (endY == -1) {
//            endY = height - 1;
//        }
//        BufferedImage result = new BufferedImage(endX - startX, endY - startY, 4);
//        for (int x = startX; x < endX; ++x) {
//            for (int y = startY; y < endY; ++y) {
//                int rgb = bufferedImage.getRGB(x, y);
//                result.setRGB(x - startX, y - startY, rgb);
//            }
//        }
//        return result;
//    }
}
