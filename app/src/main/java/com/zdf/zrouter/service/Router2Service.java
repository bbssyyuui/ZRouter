package com.zdf.zrouter.service;

import android.content.Context;

import com.zdf.zrouter.FirstActivity;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Url;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface Router2Service {

    @Activity(FirstActivity.class)
    void startFirstActivity(Context context);

    @Url("zdf://third")
    @Activity(FirstActivity.class)
    void startThirdActivity(Context context);
}
