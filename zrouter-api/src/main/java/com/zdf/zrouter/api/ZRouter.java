package com.zdf.zrouter.api;

import android.content.Context;

/**
 * Created by xiaofeng on 2017/9/3.
 */

public class ZRouter {

    private Context context;

    public static ZRouter newInstance(Context context) {
        return new ZRouter(context);
    }

    public ZRouter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public <T> T create(Class<T> service) {
        return null;
    }
}
