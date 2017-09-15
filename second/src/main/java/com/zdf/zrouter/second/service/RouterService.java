package com.zdf.zrouter.second.service;

import android.content.Intent;

import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Flag;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface RouterService {

    @Activity("com.zdf.zrouter.MainActivity")
    @Flag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
    void startMainActivity();
}
