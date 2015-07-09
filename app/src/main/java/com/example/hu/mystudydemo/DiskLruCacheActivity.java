package com.example.hu.mystudydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.hu.mystudydemo.tools.DiskLruCache;
import com.example.hu.mystudydemo.tools.Utils;

import java.io.File;
import java.io.IOException;

public class DiskLruCacheActivity extends AppCompatActivity {

    DiskLruCache mDiskLruCache = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_lru_cache);
        initDiskLruCache();

    }

    private void initDiskLruCache() {
        try {
            File cacheDir = Utils.getDiskCacheDir(this, "bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, Utils.getAppVersion(this), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
