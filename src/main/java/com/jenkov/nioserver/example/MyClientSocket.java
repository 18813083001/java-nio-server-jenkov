package com.jenkov.nioserver.example;


import java.io.IOException;
import java.net.Socket;

public class MyClientSocket {

    public static void main(String[] args) throws IOException, InterruptedException {
        String message = "1234567890ab\r\n";
        System.out.println(message);
        System.out.println(message.getBytes().length);
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i < 1;i++)
            buffer.append(message);
//        System.out.println(buffer.toString());
        int size = buffer.toString().getBytes().length;
        System.out.println(size);
        Socket socket = new Socket("localhost",9090);
        socket.setKeepAlive(true);
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: "+size+"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                ""+buffer.toString()+"";

        System.out.println("==:"+httpResponse.getBytes().length);
        int i= 0;
//        while (true){
            i++;
            Thread.sleep(1000);
//            System.out.println("发送");

            socket.getOutputStream().write(httpResponse.getBytes());
            socket.getOutputStream().flush();
            byte[] b = new byte[50];
//        socket.getOutputStream().write(httpResponse.getBytes());
//        socket.getOutputStream().flush();
            Thread.sleep(5000);
            socket.close();
//            Thread.sleep(1500);
//            socket.getInputStream().read(b);
//            System.out.println(new String(b,"utf-8"));
//             System.out.println("\r\n----");

//            Thread.sleep(50);
//            socket.getOutputStream().write(httpResponse.getBytes());
//             b = new byte[50];
//            socket.getInputStream().read(b);
//            System.out.println(new String(b,"utf-8"));
//        b = new byte[500];
//        socket.getInputStream().read(b);
//        socket.getInputStream().close();
//            Thread.sleep(1000);
//            socket.getOutputStream().write(httpResponse.getBytes());
//            socket.close();

//        }


    }
}
