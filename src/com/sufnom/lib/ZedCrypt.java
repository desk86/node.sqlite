package com.sufnom.lib;

public class ZedCrypt {
    private static final String IV = "F27D5C9927726BCEFE7510B1BDD3D137";
    private static final String SALT = "3FF2EC019C627B945225DEBAD71A01B6985FE84C95A70EB132882F88C0A59A55";
    private static final int KEY_SIZE = 128;
    private static final int ITERATION_COUNT = 100;

    public final String key;
    private final AesUtil util;

    public ZedCrypt(String key){
        this.key = key;
        this.util = new AesUtil(KEY_SIZE, ITERATION_COUNT);
    }

    public String encrypt(String rawData){
        return util.encrypt(SALT, IV, key, rawData);
    }

    public String decrypt(String rawData){
        return util.decrypt(SALT, IV, key, rawData);
    }
}
