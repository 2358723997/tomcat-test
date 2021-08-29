package com.test.netty.tomcat.nio.http;

public abstract class MyServlet {

    public void service(MyRequest request, MyResponse response) throws Exception{
       if("GET".equalsIgnoreCase(request.getMethod())){
           this.doGet(request,response);
       }else{
           this.doPost(request,response);
       }
    }


    public abstract void doGet(MyRequest request, MyResponse response) throws Exception;

    public abstract void doPost(MyRequest request, MyResponse response) throws Exception;

}
