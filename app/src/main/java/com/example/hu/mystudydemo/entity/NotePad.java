package com.example.hu.mystudydemo.entity;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by q623407474 on 2015/7/6.
 */
public class NotePad {
    public static final String AUTHORITY = "com.google.provider.NotePad";    //ContentProvider的uri

    public NotePad() {
    }

    //定义基本字段
    public static final class Notes implements BaseColumns {
        public Notes() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");

        //新的MIME类型-多个
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.note";

        // 新的MIME类型-单个
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.note";

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        //字段
        public static final String TITLE = "title";
        public static final String NOTE = "note";
        public static final String CREATEDDATE = "created";
        public static final String MODIFIEDDATE = "modified";
    }
}
