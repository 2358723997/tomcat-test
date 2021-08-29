package com.test.netty.tomcat.bio.http;

import java.io.InputStream;

import lombok.Data;

@Data
public class MyRequest {

    private String method;
    private String url;

    public MyRequest(InputStream is) {

       try{
           //拿到HTTP协议的具体内容
           String content = "";

           int length = 0;
           byte[] buff = new byte[1024];

           if((length = is.read(buff)) > 0){
               content = new String(buff,0,length);
           }
               String line = content.split("\\n")[0];
               String[] arr = line.split("\\s");

               this.method = arr[0];
               this.url = arr[1].split("\\?")[0];
               System.err.println(content);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
