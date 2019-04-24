package com.seetaface2.model;

public class SeetaImageData {
    public SeetaImageData() {

    }

    public SeetaImageData(int width, int height, int channels) {
        this.data = new byte[width * height * channels];
        this.width = width;
        this.height = height;
        this.channels = channels;
    }

    public SeetaImageData(int width, int height) {
        this(width, height, 3);
    }

    public byte[] data;
    public int width;
    public int height;
    public int channels;
}
