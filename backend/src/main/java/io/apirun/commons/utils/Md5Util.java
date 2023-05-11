package io.apirun.commons.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    public static String getMD5(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] inputByteArray = str.getBytes();
        messageDigest.update(inputByteArray, 0, inputByteArray.length);
        return bytesToString(messageDigest.digest());
    }

    public static String bytesToString(byte[] data) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        char[] temp = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            temp[i * 2] = hexDigits[b >>> 4 & 0x0f];
            temp[i * 2 + 1] = hexDigits[b & 0x0f];
        }
        return new String(temp);

    }
}
