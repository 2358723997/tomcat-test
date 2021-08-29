package com.test.netty.tomcat.nio.http;

import java.util.List;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class MyRequest {

    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public MyRequest(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public String getUrl() {

        return req.uri();
    }

    public String getMethod() {
        return req.method().name();
    }

    public Map<String, List<String>> getParameters(){
        QueryStringDecoder decoder = new QueryStringDecoder(getUrl());
        return decoder.parameters();
    }

    public List<String> getParameter(String parameterName){
        List<String> list = getParameters().get(parameterName);

        if(list == null || list.isEmpty()){
            return null;
        }else{
            return list;
        }
    }
}
