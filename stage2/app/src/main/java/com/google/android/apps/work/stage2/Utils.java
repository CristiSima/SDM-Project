package com.google.android.apps.work.stage2;

import java.util.Arrays;

public class Utils {

    public static void throw_exception_if_null(Object obj, String str) {
        if (obj == null) {
            throw ((NullPointerException) f(new NullPointerException(e(str))));
        }
    }
    private static String e(String str) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        return "Parameter specified as non-null is null: method " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ", parameter " + str;
    }

    private static <T extends Throwable> T f(T t) {
        return (T) g(t, Utils.class.getName());
    }

    static <T extends Throwable> T g(T t, String str) {
        StackTraceElement[] stackTrace = t.getStackTrace();
        int length = stackTrace.length;
        int i = -1;
        for (int i2 = 0; i2 < length; i2++) {
            if (str.equals(stackTrace[i2].getClassName())) {
                i = i2;
            }
        }
        t.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(stackTrace, i + 1, length));
        return t;
    }
}
