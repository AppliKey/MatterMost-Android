package com.applikey.mattermost.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class PrimitiveConverterFactory extends Converter.Factory {

    private static PrimitiveConverterFactory sInstance;

    private PrimitiveConverterFactory() {
    }

    public static Converter.Factory create() {
        if (sInstance == null) {
            sInstance = new PrimitiveConverterFactory();
        }
        return sInstance;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == String.class) {
            return ResponseBody::string;
        } else if (type == Integer.class) {
            return value -> Integer.valueOf(value.string());
        } else if (type == Double.class) {
            return value -> Double.valueOf(value.string());
        } else if (type == Boolean.class) {
            return value -> Boolean.valueOf(value.string());
        }
        return null;
    }
}
