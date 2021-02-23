package com.inspection.java.rpl;

import java.util.HashMap;
import java.util.Map;

public class MethodMap {
    public static Map<String, String> methodMap = new HashMap<>();
    static {
        methodMap.put(Constants.CD_QUERY_FOR_LIST, Constants.DB_QUERY_FOR_LIST);
        methodMap.put(Constants.CD_QUERY_FOR_LIST_PAGE, Constants.DB_QUERY_FOR_LIST_PAGE);
        methodMap.put(Constants.CD_QUERY, Constants.DB_QUERY);
        methodMap.put(Constants.CD_QUERY_ONE, Constants.DB_QUERY_ONE);
        methodMap.put(Constants.CD_QUERY_WITH_PAGE, Constants.DB_QUERY_WITH_PAGE);
    }
    public static String get(String key) {
        return methodMap.get(key);
    }

    public static boolean contains(String key) {
        return methodMap.containsKey(key);
    }
}
