package com.zdf.zrouter.service;

import com.zdf.zrouter.LocalActivity;
import com.zdf.zrouter.anno.Activity;
import com.zdf.zrouter.anno.Url;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface TestService {

    @Activity(LocalActivity.class)
    void startFirstActivity();

    @Url("zdf://first")
    @Activity(LocalActivity.class)
    void startThirdActivity();
}
