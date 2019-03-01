package com.jenkov.nioserver.http;

import java.io.UnsupportedEncodingException;

/**
 * Created by jjenkov on 19-10-2015.
 *
 * 请求报文和响应报文都是由以下4部分组成
 * 1.请求行
 * 2.请求头
 * 3.空行
 * 4.消息主体
 */
public class HttpUtil {

    private static final byte[] GET    = new byte[]{'G','E','T'};
    private static final byte[] POST   = new byte[]{'P','O','S','T'};
    private static final byte[] PUT    = new byte[]{'P','U','T'};
    private static final byte[] HEAD   = new byte[]{'H','E','A','D'};
    private static final byte[] DELETE = new byte[]{'D','E','L','E','T','E'};

    private static final byte[] HOST           = new byte[]{'H','o','s','t'};
    private static final byte[] CONTENT_LENGTH = new byte[]{'C','o','n','t','e','n','t','-','L','e','n','g','t','h'};
    //sharedArray包含的数据必须是请求行、消息头、消息体，且消息体的长度必须和Content——Length的值一样
    //如果消息体的实际长度大于Content——Length，那么大于部分的消息必须是下一个标准的http消息
    public static int parseHttpRequest(byte[] src, int startIndex, int endIndex, HttpHeaders httpHeaders){


        /*
        int endOfHttpMethod = findNext(src, startIndex, endIndex, (byte) ' ');
        if(endOfHttpMethod == -1) return false;
        resolveHttpMethod(src, startIndex, httpHeaders);
        */

        //parse HTTP request line 解析请求行
        int endOfFirstLine = findNextLineBreak(src, startIndex, endIndex);
        if(endOfFirstLine == -1) return -1;


        //parse HTTP headers 解析头信息
        int prevEndOfHeader = endOfFirstLine + 1; //endOfFirstLine是\n的下标，endOfFirstLine-1是\r的下标，prevEndOfHeader+1是下一行的开头字母下标
        int endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);
        // endOfHeader != prevEndOfHeader + 1 判断是否是空行，
        // 空行 http协议规定的格式，一般采用\r\n (（如果是空行：prevEndOfHeader=\r，prevEndOfHeader+1=\n）
        // 如果消息头结束了，endOfHeader 指向的下标是\r\n中的\n
        while(endOfHeader != -1 && endOfHeader != prevEndOfHeader + 1){    //prevEndOfHeader + 1 = end of previous header + 2 (+2 = CR + LF) CR=\r(回车) LF=\n（换行）

            if(matches(src, prevEndOfHeader, CONTENT_LENGTH)){
                try {
                    findContentLength(src, prevEndOfHeader, endIndex, httpHeaders);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            prevEndOfHeader = endOfHeader + 1;
            endOfHeader = findNextLineBreak(src, prevEndOfHeader, endIndex);
        }

        if(endOfHeader == -1){
            return -1;
        }

        //check that byte array contains full HTTP message.
        int bodyStartIndex = endOfHeader + 1;
        int bodyEndIndex  = bodyStartIndex + httpHeaders.contentLength;
        //endIndex是当前缓冲池里最后一个字节是下标，如果endIndex==bodyEndIndex，说明数据已经接受完了，如果bodyEndIndex < endIndex,说明数据的实际长度大于Content-Length说明的长度
        if(bodyEndIndex <= endIndex){
            //byte array contains a full HTTP request
            httpHeaders.bodyStartIndex = bodyStartIndex;
            httpHeaders.bodyEndIndex   = bodyEndIndex;
            return bodyEndIndex;
        }


       return -1;
    }

    private static void findContentLength(byte[] src, int startIndex, int endIndex, HttpHeaders httpHeaders) throws UnsupportedEncodingException {
        int indexOfColon = findNext(src, startIndex, endIndex, (byte) ':');

        //skip spaces after colon
        int index = indexOfColon +1;
        while(src[index] == ' '){
            index++;
        }

        int valueStartIndex = index;
        int valueEndIndex   = index;
        boolean endOfValueFound = false;

        while(index < endIndex && !endOfValueFound){
            switch(src[index]){
                case '0' : ;
                case '1' : ;
                case '2' : ;
                case '3' : ;
                case '4' : ;
                case '5' : ;
                case '6' : ;
                case '7' : ;
                case '8' : ;
                case '9' : { index++;  break; }

                default: {
                    endOfValueFound = true;
                    valueEndIndex = index;
                }
            }
        }

        httpHeaders.contentLength = Integer.parseInt(new String(src, valueStartIndex, valueEndIndex - valueStartIndex, "UTF-8"));

    }


    public static int findNext(byte[] src, int startIndex, int endIndex, byte value){
        for(int index = startIndex; index < endIndex; index++){
            if(src[index] == value) return index;
        }
        return -1;
    }

    public static int findNextLineBreak(byte[] src, int startIndex, int endIndex) {
        for(int index = startIndex; index < endIndex; index++){
            if(src[index] == '\n'){
                if(src[index - 1] == '\r'){
                    return index;
                }
            };
        }
        return -1;
    }

    public static void resolveHttpMethod(byte[] src, int startIndex, HttpHeaders httpHeaders){
        if(matches(src, startIndex, GET)) {
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_GET;
            return;
        }
        if(matches(src, startIndex, POST)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_POST;
            return;
        }
        if(matches(src, startIndex, PUT)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_PUT;
            return;
        }
        if(matches(src, startIndex, HEAD)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_HEAD;
            return;
        }
        if(matches(src, startIndex, DELETE)){
            httpHeaders.httpMethod = HttpHeaders.HTTP_METHOD_DELETE;
            return;
        }
    }

    public static boolean matches(byte[] src, int offset, byte[] value){
        for(int i=offset, n=0; n < value.length; i++, n++){
            if(src[i] != value[n]) return false;
        }
        return true;
    }
}
