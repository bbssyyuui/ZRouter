package com.zdf.zrouter.first.service;

import com.zdf.zrouter.anno.Activity;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface RouterService {

    @Activity("com.zdf.zrouter.second.SecondActivity")
    void startSecondActivity();
}
