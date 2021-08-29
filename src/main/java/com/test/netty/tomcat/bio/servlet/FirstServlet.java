package com.test.netty.tomcat.bio.servlet;

import com.test.netty.tomcat.bio.http.MyRequest;
import com.test.netty.tomcat.bio.http.MyResponse;
import com.test.netty.tomcat.bio.http.MyServlet;

public class FirstServlet extends MyServlet {
    public void doGet(MyRequest request, MyResponse response) throws Exception {
        this.doPost(request,response);
    }

    public void doPost(MyRequest request, MyResponse response) throws Exception {
       response.write("This is first servlet from BIO.");
    }
}
