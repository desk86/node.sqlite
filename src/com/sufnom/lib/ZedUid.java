package com.sufnom.lib;

import java.util.UUID;

public class ZedUid {
    public static String getRandomUid(){
        return UUID.randomUUID().toString();
    }
}
