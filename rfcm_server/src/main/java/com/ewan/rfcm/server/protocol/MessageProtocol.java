package com.ewan.rfcm.server.protocol;

public class MessageProtocol {
    public static final byte ROOT_DIRECTORY = 0;
    public static final byte DIRECTORY = 1;
    public static final byte FILES = 2;
    public static final byte CHANGE_FILE_NAME = 3;
    public static final byte MOVE_COPY_FILE = 4;
    public static final byte DELETE_FILE = 5;
    public static final byte FILE_UPLOAD = 6;
    public static final byte FILE_DOWN_LOAD = 7;
}
