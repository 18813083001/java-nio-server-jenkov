package com.jenkov.nioserver.http;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by chenlinsong on 2019/2/28.
 */
public class SocketOne {
    @Test
    public void socketSend() throws IOException {
        String message = "1234567890ab\r\n";
        System.out.println(message);
        System.out.println(message.getBytes().length);
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i < 100000;i++)
            buffer.append(message);
        int size = buffer.toString().getBytes().length;
        System.out.println(size);
        Socket socket = new Socket("localhost",9090);
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: "+size+"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                ""+buffer.toString()+"";

        System.out.println("==:"+httpResponse.getBytes().length);
        socket.getOutputStream().write(httpResponse.getBytes());
        socket.getOutputStream().flush();
        socket.close();
    }

    @Test
    public void socketSendReceived() throws IOException, InterruptedException {
        String message = "1234567890ab\r\n";
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i < 1;i++)
            buffer.append(message);
        Socket socket = new Socket("localhost",9090);
        socket.setKeepAlive(true);
        int size = buffer.toString().getBytes().length;
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: "+size+"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                ""+buffer.toString()+"";

        socket.getOutputStream().write(httpResponse.getBytes());
        socket.getOutputStream().flush();

        byte[] b = new byte[110];
        InputStream inputStream = socket.getInputStream();
        int len;
        while ((len = inputStream.read(b)) != -1){
            String aa = new String(b,0,len,"utf-8");
            System.out.println(aa);
        }

        socket.close();
        Thread.sleep(1000);
    }

    @Test
    public void socketNio() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost", 9090));

        writeBuffer(socketChannel);
        readBuffer(socketChannel);

        System.out.println("-------");

        writeBuffer(socketChannel);
        readBuffer(socketChannel);
//        socketChannel.close();
    }

    @Test
    public void socketNioLoop() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost", 9090));



        writeBuffer(socketChannel);
        readBuffer(socketChannel);

        System.out.println("-------");

        writeBuffer(socketChannel);
        readBuffer(socketChannel);
//        socketChannel.close();
    }

    public void writeBuffer(SocketChannel socketChannel) throws IOException {
        String message = "1234567890ab\r\n";
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i < 1;i++)
            buffer.append(message);
        int size = buffer.toString().getBytes().length;
        String writeMessage = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: "+size+"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                ""+buffer.toString()+"";


        ByteBuffer buf = ByteBuffer.allocate(writeMessage.getBytes().length);
        buf.clear();
        buf.put(writeMessage.toString().getBytes());
        buf.flip();

        while(! socketChannel.finishConnect() ){
            System.out.println("wait connect！");
        }
        while(buf.hasRemaining()) {
            socketChannel.write(buf);
        }
        buf.clear();
    }

    public void readBuffer(SocketChannel socketChannel) throws IOException {
        ByteBuffer readBuf = ByteBuffer.allocate(111);

        int bytesRead = socketChannel.read(readBuf);
        int totalBytesRead = bytesRead;
        while(bytesRead != -1){
            bytesRead = socketChannel.read(readBuf);
            totalBytesRead += bytesRead;
        }
        byte[] bytes = readBuf.array();
        String aa = new String(bytes,0,totalBytesRead,"utf-8");
        System.out.println(aa);
        readBuf.clear();
    }
}
