package com.ewan.rfcm.global.constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserConnection {
    public static Map userConnections = new ConcurrentHashMap<String, String>();
}