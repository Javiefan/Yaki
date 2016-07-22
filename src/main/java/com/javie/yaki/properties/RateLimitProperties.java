package com.javie.yaki.properties;

/**
 * Created by Javie on 16/7/1.
 */
public class RateLimitProperties {
    private int timeout;
    private float rate;
    private float bucket;

    public int getTimeout() {

        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getBucket() {
        return bucket;
    }

    public void setBucket(float bucket) {
        this.bucket = bucket;
    }

}
