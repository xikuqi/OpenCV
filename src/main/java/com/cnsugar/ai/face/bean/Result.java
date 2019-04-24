package com.cnsugar.ai.face.bean;

import com.seetaface2.model.RecognizeResult;

import java.io.Serializable;

/**
 * @Author Sugar
 * @Version 2019/4/22 17:50
 * @Copyright 上海云辰信息科技有限公司
 */
public class Result implements Serializable {
    private String key;
    private float similar;

    public Result() {

    }

    public Result(RecognizeResult result) {
        this.similar = result.similar;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    @Override
    public String toString() {
        return "{" +
                "" + key +
                ": " + similar +
                '}';
    }
}
