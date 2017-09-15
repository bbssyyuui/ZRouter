package com.zdf.zrouter.first;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zdf.zrouter.first.service.RouterService;
import com.zdf.zrouter.anno.ZService;

public class FirstActivity extends AppCompatActivity {

    @ZService
    RouterService routerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

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
                routerService.startSecondActivity();
            }
        });
    }
}
