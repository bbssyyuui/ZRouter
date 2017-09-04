package com.zdf.zrouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zdf.zrouter.anno.AnnoTest;
import com.zdf.zrouter.api.ZRouter;

@AnnoTest
public class MainActivity extends AppCompatActivity {

    private RouterService routerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        routerService = new ZRouter().create(RouterService.class);
        new ZRouter().test(RouterService.class);

        Button btnJump1 = (Button) findViewById(R.id.btn_jump1);
        Button btnJump2 = (Button) findViewById(R.id.btn_jump2);

        btnJump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerService.startFirstActivity(MainActivity.this);
            }
        });

        btnJump2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void startFirstActivity() {
        Intent intent = new Intent();
        intent.setClass(this, FirstActivity.class);
        startActivity(intent);
    }
}
