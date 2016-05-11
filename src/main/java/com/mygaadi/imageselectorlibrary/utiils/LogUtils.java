package com.mygaadi.imageselectorlibrary.utiils;

import android.util.Log;

/**
 * Created by Ambujesh on 10/14/2015.
 */
public class LogUtils {

    private static final int STACK_TRACE_LEVELS_UP = 5;

    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getLineNumber();
    }

    private static String getClassName() {
        String fileName = Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getFileName();
        return fileName.substring(0, fileName.length() - 5);
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getMethodName();
    }

    private static String getClassNameMethodNameAndLineNumber() {
        return "[" + getClassName() + "." + getMethodName() + "()-" + getLineNumber() + "]: ";
    }

    public static void d(final String tag, String message) {
        if (!Constants.ENVIRONMENT.equals("PROD"))
            Log.d(tag, getClassNameMethodNameAndLineNumber() + message);
    }

    public static void d(final String tag, String message, Throwable cause) {
        if (!Constants.ENVIRONMENT.equals("PROD"))
            Log.d(tag, getClassNameMethodNameAndLineNumber() + message, cause);
    }

    /**
     * The Android development tools provide the BuildConfig.DEBUG flag for this purpose.
     * This flag will be automatically set to false if you export the Android application for deployment.
     * During development it will be set to true, therefore allowing you to see your logging statements during development.
     */

    public static void v(final String tag, String message) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.v(tag, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void v(final String tag, String message, Throwable cause) {

        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.v(tag, getClassNameMethodNameAndLineNumber() + message, cause);
        }
    }

    public static void i(final String tag, String message) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.i(tag, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void i(final String tag, String message, Throwable cause) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.i(tag, getClassNameMethodNameAndLineNumber() + message, cause);
        }
    }

    public static void w(final String tag, String message) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.w(tag, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void w(final String tag, String message, Throwable cause) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.w(tag, getClassNameMethodNameAndLineNumber() + message, cause);
        }
    }

    public static void e(final String tag, String message) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.e(tag, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void e(String message) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.e(getClassNameMethodNameAndLineNumber(), message);
        }
    }

    public static void e(int message) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.e(getClassNameMethodNameAndLineNumber(), String.valueOf(message));
        }
    }

    public static void e(final String tag, String message, Throwable cause) {
        if (!Constants.ENVIRONMENT.equals("PROD")) {
            Log.e(tag, getClassNameMethodNameAndLineNumber() + message, cause);
        }
    }

}
