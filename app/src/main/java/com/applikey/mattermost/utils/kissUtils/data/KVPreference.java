package com.applikey.mattermost.utils.kissUtils.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.applikey.mattermost.utils.kissUtils.utils.FileUtil;

public class KVPreference implements KVDataSet {

    private static final String TAG = KVPreference.class.getSimpleName();

    private static final String DEFAULT_NAME = "kv_sp";

    private final SharedPreferences sp;
    private final String spName;
    private final Context spContext;

    public KVPreference(Context context) {
        this(context, DEFAULT_NAME);
    }

    public KVPreference(Context context, String name) {
        this.spContext = context;
        this.spName = name;
        sp = spContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    @Override
    public String get(String key) {
        //noinspection UnnecessaryLocalVariable
        final String value = sp.getString(key, null);
        return value;
    }

    @Override
    public boolean set(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return false;
        }

        //noinspection UnnecessaryLocalVariable
        final boolean succeed = sp.edit().putString(key, value).commit();
        return succeed;
    }

    @Override
    public boolean remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        //noinspection UnnecessaryLocalVariable
        final boolean succeed = sp.edit().remove(key).commit();
        return succeed;
    }

    @Override
    public boolean clear() {
        //noinspection UnnecessaryLocalVariable
        final boolean succeed = sp.edit().clear().commit();
        return succeed;
    }

    @Override
    public boolean delete() {
        boolean succeed = clear();
        final String absPath = spContext.getFilesDir().getParent() + "/shared_prefs/"
                + spName + ".xml";
        succeed |= FileUtil.delete(absPath);
        return succeed;
    }
}
