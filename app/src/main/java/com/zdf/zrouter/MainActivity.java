package com.zdf.zrouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zdf.zrouter.anno.AnnoTest;

@AnnoTest
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
