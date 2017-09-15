package com.zdf.zrouter.service;

import android.content.Intent;
import android.provider.MediaStore;

import com.zdf.zrouter.LocalActivity;
import com.zdf.zrouter.anno.Action;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Flag;
import com.zdf.zrouter.anno.Path;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface RouterService {

    @Activity(LocalActivity.class)
    void startLocalActivity();

    @Path("com.zdf.zrouter.first.FirstActivity")
    void startFirstActivity();

    @Path("com.zdf.zrouter.second.SecondActivity")
    void startSecondActivity();

    @Action(MediaStore.ACTION_IMAGE_CAPTURE)
    void startCapture();

    @Action(Intent.ACTION_DIAL)
    @Path("tel:phone")
    @Flag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
    void startTel();
}
