package com.ewan.rfcm.global.constant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class UserConnection {
    public static Map userConnections = new ConcurrentHashMap<String, String>();
}