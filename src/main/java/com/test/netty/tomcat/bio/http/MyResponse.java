package com.test.netty.tomcat.bio.http;

import java.io.OutputStream;

public class MyResponse {
    private OutputStream out;

    public MyResponse(OutputStream os) {
        this.out = os;
    }

    public void write(String s) throws Exception{
        StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/1.1 200 ok\n")
              .append("Content-Type: text/html;\n")
              .append("\r\n")
              .append(s);
        out.write(buffer.toString().getBytes());
    }

}
