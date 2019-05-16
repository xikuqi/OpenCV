package com.cnsugar.ai.face.bean;

import java.io.Serializable;

/**
 * @Author Sugar
 * @Version 2019/4/22 17:14
 */
public class FaceIndex implements Serializable {
    private String key;

    private int index;

    private byte[] imgData;
    private int width;
    private int height;
    private int channel;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
