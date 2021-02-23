package com.inspection.java.db.method;

public class SessionMethodUtils {
    public static boolean isOpenSessionOperation(String className, String methodName) {
        return OpenSessionMethodConstants.CLASS_NAME.equals(className)
                && OpenSessionMethodConstants.METHOD_NAME.equals(methodName);
    }
    public static boolean isCloseSessionOperation(String className, String methodName) {
        return CloseSessionMethodConstants.CLASS_NAME.equals(className)
                && CloseSessionMethodConstants.METHOD_NAME.equals(methodName);
    }
}
