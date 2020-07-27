package com.headmostlab.quickbarbell.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WOW on 10/01/2015.
 */
public class LogMethod {

    private enum MsgType {
        ERROR,
        DEBUG
    }

    private static final Map<Long, StringBuilder> stringBuilderMap = new HashMap<>();

    static public void d () {
        helper(MsgType.DEBUG, new Throwable(), null);
    }

    static public void d (String msg) {
        helper(MsgType.DEBUG, new Throwable(), msg);
    }

    static public void e () {
        helper(MsgType.ERROR, new Throwable(), null);
    }

    static public void e (String msg) {
        helper(MsgType.ERROR, new Throwable(), msg);
    }

    private static void helper(MsgType msgType, Throwable throwable, String msg) {
        String threadId = Thread.currentThread().getName();
        final StringBuilder sb = getStringBuilder();
        StackTraceElement traceElement = throwable.getStackTrace()[1];
        String tag = sb.append(">>> ").append(getSimpleClassName(traceElement.getClassName())).toString();
        sb.setLength(0);
        String fullMsg = sb.append(traceElement.getMethodName()).append(" ").append(msg!=null?msg:"")
                .append(" L").append(traceElement.getLineNumber()).append(" T").append(threadId).toString();
        switch (msgType) {
            case DEBUG:
                Log.d(tag, fullMsg);
                break;
            case ERROR:
                Log.e(tag, fullMsg);
                break;
        }
    }

    private static String getSimpleClassName(String className) {
        return className.substring(className.lastIndexOf(".")+1);
    }

    private static StringBuilder getStringBuilder() {
        StringBuilder stringBuilder = stringBuilderMap.get(Thread.currentThread().getId());
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
            stringBuilderMap.put(Thread.currentThread().getId(), stringBuilder);
        }
        stringBuilder.setLength(0);
        return stringBuilder;
    }
}