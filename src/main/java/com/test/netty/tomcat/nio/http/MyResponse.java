package com.test.netty.tomcat.nio.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.apache.commons.lang3.StringUtils;

public class MyResponse {

    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public MyResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String s) {
        if (StringUtils.isBlank(s)) {
            return;
        }

        try {
            FullHttpResponse response = new DefaultFullHttpResponse(
                //设置HTTP版本号为1.1
                HttpVersion.HTTP_1_1,
                //设置返回HTTP状态码
                HttpResponseStatus.OK,
                //统一输出格式为UTF-8
                Unpooled.wrappedBuffer(s.getBytes("UTF-8"))
            );
            response.headers().set("Content-Type", "text/html");

            ctx.write(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ctx.flush();
            ctx.close();
        }
    }
}
