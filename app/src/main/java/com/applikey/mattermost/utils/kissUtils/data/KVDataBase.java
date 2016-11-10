package com.applikey.mattermost.utils.kissUtils.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.applikey.mattermost.utils.kissUtils.utils.FileUtil;

import java.util.LinkedList;
import java.util.List;


public class KVDataBase extends SQLiteOpenHelper implements KVDataSet {

    public static final String TAG = "KVDataBase";

    private static final int DB_VERSION = 1;
    private static final String DEFAULT_DB_NAME = "kv_data.db";
    private static final String DEFAULT_TABLE_NAME = "key_value";

    private final Context dbContext;
    private final String dbName;

    public KVDataBase(Context context) {
        this(context, DEFAULT_DB_NAME);
    }

    public KVDataBase(Context context, String name) {
        super(context, name, null, DB_VERSION);
        this.dbName = name;
        this.dbContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DEFAULT_TABLE_NAME
                + "(id INTEGER, key TEXT UNIQUE, value TEXT NOT NULL, PRIMARY KEY(id, key));");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DEFAULT_TABLE_NAME);

        onCreate(db);
    }

    public String get(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        final String sql = "SELECT * FROM " + DEFAULT_TABLE_NAME + " WHERE key='"
                + key + "'";
        final SQLiteDatabase db = getWritableDatabase();
        final Cursor cursor = db.rawQuery(sql, null);
        String value = null;
        if (cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndex("value"));
        }
        cursor.close();
        db.close();

        return value;
    }

    public boolean set(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return false;
        }

        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT OR REPLACE INTO " + DEFAULT_TABLE_NAME
                + "(key, value) VALUES(?, ?)", new Object[] {key, value});
        db.close();
        return true;
    }

    public boolean remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        final String sql = "DELETE FROM " + DEFAULT_TABLE_NAME + " WHERE key " + "='"
                + key + "'";
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
        return true;
    }

    @Override
    public boolean clear() {
        final String sql = "DELETE * FROM " + DEFAULT_TABLE_NAME;
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
        return true;
    }

    @Override
    public boolean delete() {
        final String absPath = dbContext.getFilesDir().getParent() + "/databases/" + dbName;
        //noinspection UnnecessaryLocalVariable
        boolean succeed = FileUtil.delete(absPath);
        return succeed;
    }

    public List<String> list() {
        final String sql = "SELECT * FROM " + DEFAULT_TABLE_NAME
                + " ORDER BY id  ASC";
        final SQLiteDatabase db = getWritableDatabase();
        final Cursor cursor = db.rawQuery(sql, null);
        final LinkedList<String> values = new LinkedList<String>();
        while (cursor.moveToNext()) {
            String value = cursor.getString(cursor.getColumnIndex("value"));
            values.addLast(value);
        }
        cursor.close();
        db.close();
        return values;
    }
}
