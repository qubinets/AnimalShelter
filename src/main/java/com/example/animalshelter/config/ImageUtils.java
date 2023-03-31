package com.example.animalshelter.config;


import org.apache.tomcat.util.codec.binary.Base64;

public class ImageUtils {

    public static String encodeToBase64(byte[] data) {
        return Base64.encodeBase64String(data);
    }

}