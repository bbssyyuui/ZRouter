package com.zdf.zrouter.service;

import android.content.Context;
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
    void startFirstActivity(Context context);

    @Url("zdf://second")
    void startSecondActivity(Context context);

    @Action(MediaStore.ACTION_IMAGE_CAPTURE)
    void startCapture(Context context);

    @Action(Intent.ACTION_DIAL)
    @Url("tel:{phone}")
    void startTel(Context context);
}
