package com.jenkov.nioserver.example;

/**
 * Created by chenlinsong on 2019/2/28.
 */
public class ByteTest {
    public static void main(String[] args) {
        String message = "1234567890ab\r\n";
        System.out.println(message);
        System.out.println(message.getBytes().length);
    }
}
