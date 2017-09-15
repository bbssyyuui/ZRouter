package com.zdf.zrouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zdf.zrouter.anno.ZService;
import com.zdf.zrouter.service.RouterService;

public class LocalActivity extends AppCompatActivity {

    @ZService
    public RouterService routerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        Button btnBack = (Button) findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
