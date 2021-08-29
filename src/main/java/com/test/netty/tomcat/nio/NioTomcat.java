package com.test.netty.tomcat.nio;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import com.test.netty.tomcat.nio.http.MyServlet;
import com.test.netty.tomcat.nio.http.MyRequest;
import com.test.netty.tomcat.nio.http.MyResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * NioTomcat类
 *
 * @author wangjixue
 * @date 8/29/21 4:19 PM
 */
public class NioTomcat {

    private static final int DEFAULT_PORT = 8081;

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("web-nio", Locale.getDefault());
    private HashMap<String, MyServlet> servletMapping = new HashMap<>();

    public static void main(String[] args) {
        new NioTomcat().start();
    }

    /**
     * Tomcat的启动入口
     */
    private void start() {
        try {
            //1、加载web.properties文件，解析配置
            init();
            //Boss线程
            EventLoopGroup boss = new NioEventLoopGroup();
            //Worker线程
            EventLoopGroup worker = new NioEventLoopGroup();
            //2、创建Netty服务端对象
            ServerBootstrap server = new ServerBootstrap();
            //3、配置服务端参数
            server.group(boss, worker)
                //配置主线程的处理逻辑
                .channel(NioServerSocketChannel.class)
                //子线程的回调处理，Handler
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel client) {
                        //处理回调的逻辑

                        //链式编程，责任链模式
                        //处理响应结果的封装
                        client.pipeline().addLast(new HttpResponseEncoder())
                            //用户请求过来，要解码
                            .addLast(new HttpRequestDecoder())
                            //用户自己的业务逻辑
                            .addLast(new MyTomcatHadler());

                    }
                })
                //配置主线程分配的最大线程数
                .option(ChannelOption.SO_BACKLOG, 128)
                //保持长连接
                .option(ChannelOption.SO_KEEPALIVE, true);
            //启动服务
            ChannelFuture future = server.bind(DEFAULT_PORT).sync();

            System.out.println("NIO Tomcat 已启动，监听端口是: " + this.DEFAULT_PORT);

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Enumeration<String> keys = RESOURCE_BUNDLE.getKeys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.endsWith(".url")) {
                //将 servlet.xxx.url 的 .url 替换，只剩下 servlet.xxx当成  servletName
                String servletName = key.replaceAll("\\.url$", "");
                String url = RESOURCE_BUNDLE.getString(key);

                //拿到Serlvet的全类名
                String className = RESOURCE_BUNDLE.getString(servletName + ".className");
                //反射创建Servlet的实例
                MyServlet servlet = (MyServlet)Class.forName(className).newInstance();
                //将URL和Servlet建立映射关系
                servletMapping.put(url, servlet);
            }
        }

    }

    public class MyTomcatHadler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;

                MyRequest request = new MyRequest(ctx,req);
                MyResponse response = new MyResponse(ctx,req);

                String url = request.getUrl();

                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request,response);
                }else{
                    response.write("404 - Not Found!!");
                }
            }

        }
    }

}


