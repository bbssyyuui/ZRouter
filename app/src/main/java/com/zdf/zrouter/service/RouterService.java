package com.zdf.zrouter.service;

import android.content.Intent;
import android.provider.MediaStore;

import com.zdf.zrouter.LocalActivity;
import com.zdf.zrouter.anno.Action;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Class;
import com.zdf.zrouter.anno.Url;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface RouterService {

    @Class(LocalActivity.class)
//    @Anim(in = android.R.anim.fade_in, out = android.R.anim.fade_out)
    void startLocalActivity();

    @Activity("com.zdf.zrouter.first.FirstActivity")
    void startFirstActivity();

    @Activity("com.zdf.zrouter.second.SecondActivity")
//    @Path("second")
    void startSecondActivity();

    @Action(MediaStore.ACTION_IMAGE_CAPTURE)
    void startCapture();

    @Action(Intent.ACTION_DIAL)
    @Url("tel:phone")
    void startTel();
}
