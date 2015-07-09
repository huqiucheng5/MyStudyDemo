package com.example.hu.mystudydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onView(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, VolleryActivity.class);
        startActivity(intent);

    }

    public void onAnim(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, AnimationTestActivity.class);
        startActivity(intent);

    }

}
