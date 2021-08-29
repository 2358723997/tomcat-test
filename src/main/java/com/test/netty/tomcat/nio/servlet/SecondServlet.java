package com.test.netty.tomcat.nio.servlet;

import com.test.netty.tomcat.nio.http.MyRequest;
import com.test.netty.tomcat.nio.http.MyResponse;
import com.test.netty.tomcat.nio.http.MyServlet;

public class SecondServlet extends MyServlet {
    public void doGet(MyRequest request, MyResponse response) throws Exception {
        this.doPost(request,response);
    }

    public void doPost(MyRequest request, MyResponse response) throws Exception {
        response.write("This is second servlet from NIO.");
    }
}
