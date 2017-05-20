package com.example.andrey.commandshandler;

/**
 * Created by andrey on 14/05/17.
 */

public class Utils {
    public static byte[] hexStringToByteArray(String hexStr) {
        int len = hexStr.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4) + Character.digit(hexStr.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] intToByteArray(int val) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (val & 0xFF);
        ret[2] = (byte) ((val >> 8) & 0xFF);
        ret[1] = (byte) ((val >> 16) & 0xFF);
        ret[0] = (byte) ((val >> 24) & 0xFF);
        return ret;
    }

    public static byte[] intToByteArray2(int val) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (val & 0xFF);
        ret[1] = (byte) ((val >> 8) & 0xFF);
        ret[2] = (byte) ((val >> 16) & 0xFF);
        ret[3] = (byte) ((val >> 24) & 0xFF);
        return ret;
    }

    public static int byteArrayToInt(byte[] b, int offset){
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[offset + i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static int byteArrayToInt2(byte[] b, int offset){
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (i) * 8;
            value += (b[offset + i] & 0x000000FF) << shift;
        }
        return value;
    }

    public static byte[] shortToByteArray(short val) {
        byte[] ret = new byte[2];
        ret[1] = (byte) (val & 0xFF);
        ret[0] = (byte) ((val >> 8) & 0xFF);

        return ret;
    }

    public static byte[] shortToByteArray1(short val) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (val & 0xFF);
        ret[1] = (byte) ((val >> 8) & 0xFF);

        return ret;
    }

    public static void hexdump(byte[] buf, String prefix, int offset, int length) {
        System.out.printf("\n" + prefix + "%04X:  ", 0);
        for (int i = offset; i < length; i++) {
            System.out.printf("%02x ", buf[i]);
            if ( (i+1) % 0x10 == 0 )
                System.out.printf("\n" + prefix + "%04X:  ", i+1);
        }
        System.out.println();
    }

    public static void hexdump(byte[] buf) {
        hexdump(buf, "", 0, buf.length);
    }

    public static String hexdumptoString(byte[] buf, String prefix, int offset, int length) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("\n" + prefix + "%04X:  ", 0));
        for (int i = offset; i < length; i++) {
            sb.append(String.format("%02x ", buf[i]));
            if ( (i+1) % 0x10 == 0 )
                sb.append(String.format("\n" + prefix + "%04X:  ", i+1));
        }
        return sb.toString();
    }

    public static long bytetoLong(byte[] buf){
        if (buf != null && buf.length == 4) {
            return
                    ((long)(buf[0] & 0xff) << 24) |
                            ((long)(buf[1] & 0xff) << 16) |
                            ((long)(buf[2] & 0xff) << 8) |
                            ((long)(buf[3] & 0xff));
        } else {
            return 0;
        }
    }

}
