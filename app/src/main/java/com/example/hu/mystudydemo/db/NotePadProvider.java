package com.example.hu.mystudydemo.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.hu.mystudydemo.entity.NotePad;
import com.example.hu.mystudydemo.entity.NotePad.Notes;

import java.util.HashMap;

/**
 * 内容提供者
 * Created by q623407474 on 2015/7/6.
 */
public class NotePadProvider extends ContentProvider {
    private static final String TAG = "NotePadProvider";
    // 数据库名
    private static final String DATABASE_NAME = "note_pad.db";
    private static final int DATABASE_VERSION = 1;
    // 表名
    private static final String NOTES_TABLE_NAME = "notes";
    private static HashMap<String, String> sNotesProjectionMap;
    private static final int NOTES = 1;
    private static final int NOTE_ID = 2;
    private static final UriMatcher sUriMatcher;
    private DatabaseHelper mOpenHelper;

    //创建表SQL语句
    private static final String CREATE_TABLE = "CREATE TABLE "
            + NOTES_TABLE_NAME
            + " (" + Notes._ID
            + " INTEGER PRIMARY KEY,"
            + Notes.TITLE
            + " TEXT,"
            + Notes.NOTE
            + " TEXT,"
            + Notes.CREATEDDATE
            + " INTEGER,"
            + Notes.MODIFIEDDATE
            + " INTEGER" + ");";

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(NotePad.AUTHORITY, "notes", NOTES);
        sUriMatcher.addURI(NotePad.AUTHORITY, "notes/#", NOTE_ID);

        sNotesProjectionMap = new HashMap<String, String>();
        sNotesProjectionMap.put(Notes._ID, Notes._ID);
        sNotesProjectionMap.put(Notes.TITLE, Notes.TITLE);
        sNotesProjectionMap.put(Notes.NOTE, Notes.NOTE);
        sNotesProjectionMap.put(Notes.CREATEDDATE, Notes.CREATEDDATE);
        sNotesProjectionMap.put(Notes.MODIFIEDDATE, Notes.MODIFIEDDATE);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        //构造函数-创建数据库
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //创建表
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        //更新数据库
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                qb.setTables(NOTES_TABLE_NAME);
                qb.setProjectionMap(sNotesProjectionMap);
                break;

            case NOTE_ID:
                qb.setTables(NOTES_TABLE_NAME);
                qb.setProjectionMap(sNotesProjectionMap);
                qb.appendWhere(Notes._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = NotePad.Notes.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /**
     * 如果有自定义类型，必须实现该方法
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                return Notes.CONTENT_TYPE;

            case NOTE_ID:
                return Notes.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        Long now = Long.valueOf(System.currentTimeMillis());

        if (values.containsKey(NotePad.Notes.CREATEDDATE) == false) {
            values.put(NotePad.Notes.CREATEDDATE, now);
        }
        if (values.containsKey(NotePad.Notes.MODIFIEDDATE) == false) {
            values.put(NotePad.Notes.MODIFIEDDATE, now);
        }
        if (values.containsKey(NotePad.Notes.TITLE) == false) {
            Resources r = Resources.getSystem();
            values.put(NotePad.Notes.TITLE, r.getString(android.R.string.untitled));
        }
        if (values.containsKey(NotePad.Notes.NOTE) == false) {
            values.put(NotePad.Notes.NOTE, "");
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(NOTES_TABLE_NAME, Notes.NOTE, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(NotePad.Notes.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                count = db.delete(NOTES_TABLE_NAME, selection, selectionArgs);
                break;

            case NOTE_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(NOTES_TABLE_NAME, Notes._ID + "=" + noteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                count = db.update(NOTES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case NOTE_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(NOTES_TABLE_NAME, values, Notes._ID + "=" + noteId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
