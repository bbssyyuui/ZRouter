package com.zdf.zrouter;

import android.content.Context;

import com.zdf.zrouter.anno.Activity;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public interface RouterService {

    @Activity(FirstActivity.class)
    void startFirstActivity(Context context);
}
