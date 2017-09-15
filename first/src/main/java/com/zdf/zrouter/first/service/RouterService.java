package com.zdf.zrouter.first.service;

import com.zdf.zrouter.anno.Path;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface RouterService {

    @Path("com.zdf.zrouter.second.SecondActivity")
    void startSecondActivity();
}
