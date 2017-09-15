package com.zdf.zrouter.first;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zdf.zrouter.anno.Param;
import com.zdf.zrouter.anno.ZService;
import com.zdf.zrouter.first.service.RouterService;

public class FirstActivity extends AppCompatActivity {

    @ZService
    RouterService routerService;

    @Param("param1")
    String p1;
    @Param("param2")
    int p2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        p1 = getIntent().getStringExtra("param1");
        p2 = getIntent().getIntExtra("param2", 0);
        Toast.makeText(this, "param1 = " + p1 + ", param2 = " + p2, Toast.LENGTH_SHORT).show();

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
