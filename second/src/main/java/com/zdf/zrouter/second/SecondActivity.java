package com.zdf.zrouter.second;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zdf.zrouter.anno.ZService;
import com.zdf.zrouter.second.service.RouterService;

public class SecondActivity extends AppCompatActivity {

    @ZService
    RouterService routerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button btnBack = (Button) findViewById(R.id.btn_back);
        Button btnJump1 = (Button) findViewById(R.id.btn_jump1);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnJump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerService.startMainActivity();
            }
        });
    }
}
