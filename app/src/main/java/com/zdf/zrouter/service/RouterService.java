package com.zdf.zrouter.service;

import android.content.Intent;
import android.provider.MediaStore;

import com.zdf.zrouter.FirstActivity;
import com.zdf.zrouter.anno.Action;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Url;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface RouterService {

    @Url("zdf://first")
    @Activity(FirstActivity.class)
    void startFirstActivity();

    @Url("zdf://second")
    void startSecondActivity();

    @Action(MediaStore.ACTION_IMAGE_CAPTURE)
    void startCapture();

    @Action(Intent.ACTION_DIAL)
    @Url("tel:{phone}")
    void startTel();
}
