package com.zdf.zrouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zdf.zrouter.anno.ZService;
import com.zdf.zrouter.service.RouterService;
import com.zdf.zrouter.service.TestService;

public class MainActivity extends AppCompatActivity {

    @ZService
    RouterService routerService;

    @ZService
    TestService testService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String test = "com.zdf.zrouter.BuildConfig";
        String sub = test.substring(0, test.indexOf("BuildConfig") - 1);
        Log.v("zdf", "sub = " + sub);

        // routerService = ZRouter.newInstance(this).create(RouterService.class);

        Button btnJump1 = (Button) findViewById(R.id.btn_jump1);
        Button btnJump2 = (Button) findViewById(R.id.btn_jump2);
        Button btnJump3 = (Button) findViewById(R.id.btn_jump3);
        Button btnJump4 = (Button) findViewById(R.id.btn_jump4);
        Button btnJump5 = (Button) findViewById(R.id.btn_jump5);

        btnJump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerService.startLocalActivity();
            }
        });

        btnJump2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerService.startFirstActivity();
            }
        });

        btnJump3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerService.startSecondActivity();
            }
        });

        btnJump4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerService.startCapture();
            }
        });

        btnJump5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // routerService.startTel();
            }
        });
    }
}
