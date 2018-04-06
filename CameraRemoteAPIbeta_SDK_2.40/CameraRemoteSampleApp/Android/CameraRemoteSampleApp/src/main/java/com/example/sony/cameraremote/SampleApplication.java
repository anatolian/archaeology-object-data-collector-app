/*
 * Copyright 2014 Sony Corporation
 */

package com.example.sony.cameraremote;

import android.app.Application;

import java.util.Set;

/**
 * Application class for the sample application.
 */
public class SampleApplication extends Application {

    private ServerDevice mTargetDevice;

    private SimpleRemoteApi mRemoteApi;

    private SimpleCameraEventObserver mEventObserver;

    private Set<String> mSupportedApiSet;

    /**
     * Sets a target ServerDevice object.
     * 
     * @param device
     */
    public void setTargetServerDevice(ServerDevice device) {
        mTargetDevice = device;
    }

    /**
     * Returns a target ServerDevice object.
     * 
     * @return return ServiceDevice
     */
    public ServerDevice getTargetServerDevice() {
        return mTargetDevice;
    }

    /**
     * Sets a SimpleRemoteApi object to transmit to Activity.
     * 
     * @param remoteApi
     */
    public void setRemoteApi(SimpleRemoteApi remoteApi) {
        mRemoteApi = remoteApi;
    }

    /**
     * Returns a SimpleRemoteApi object.
     * 
     * @return return SimpleRemoteApi
     */
    public SimpleRemoteApi getRemoteApi() {
        return mRemoteApi;
    }

    /**
     * Sets a List of supported APIs.
     * 
     * @param apiList
     */
    public void setSupportedApiList(Set<String> apiList) {
        mSupportedApiSet = apiList;
    }

    /**
     * Returns a list of supported APIs.
     * 
     * @return Returns a list of supported APIs.
     */
    public Set<String> getSupportedApiList() {
        return mSupportedApiSet;
    }

    /**
     * Sets a SimpleCameraEventObserver object to transmit to Activity.
     *
     * @param observer
     */
    public void setCameraEventObserver(SimpleCameraEventObserver observer) {
        mEventObserver = observer;
    }

    /**
     * Returns a SimpleCameraEventObserver object.
     *
     * @return return SimpleCameraEventObserver
     */
    public SimpleCameraEventObserver getCameraEventObserver() {
        return mEventObserver;
    }
}
