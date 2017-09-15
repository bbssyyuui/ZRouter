package com.zdf.zrouter.service;

import com.zdf.zrouter.LocalActivity;
import com.zdf.zrouter.anno.Class;
import com.zdf.zrouter.anno.Url;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface TestService {

    @Class(LocalActivity.class)
    void startFirstActivity();

    @Url("zdf://first")
    @Class(LocalActivity.class)
    void startThirdActivity();
}
