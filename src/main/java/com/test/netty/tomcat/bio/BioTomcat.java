package com.test.netty.tomcat.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.test.netty.tomcat.bio.http.MyRequest;
import com.test.netty.tomcat.bio.http.MyResponse;
import com.test.netty.tomcat.bio.http.MyServlet;

/**
 * NioTomcat类
 *
 * @author wangjixue
 * @date 8/29/21 4:19 PM
 */


public class BioTomcat implements Runnable {

    private static volatile  boolean flag = true;

    private static int port = 8080;

    private static ServerSocket socket;

    /**
     * 2、启动服务端Socket，等待用户请求
     */
    static {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("web-bio", Locale.getDefault());

    private static ConcurrentHashMap<String, MyServlet> servletMapping = new ConcurrentHashMap<String, MyServlet>();

    /**
     * Tomcat的启动入口
     *
     * @param args
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        //1、加载web-bio.properties文件，解析配置
        init();

        while(flag){
            executor.execute(() -> {
                try {
                    new BioTomcat().run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }


    private static void init() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
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
                MyServlet servlet = (MyServlet) Class.forName(className).newInstance();
                //将URL和Servlet建立映射关系
                servletMapping.put(url, servlet);
            }
        }

    }

    private void process(Socket client) throws Exception {

        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream();
        MyRequest request = new MyRequest(in);
        MyResponse response = new MyResponse(out);

        String url = request.getUrl();

        if(servletMapping.containsKey(url)){
            servletMapping.get(url).service(request,response);
        }else{
            response.write("404 - Not Found!!");
        }
        out.flush();
        out.close();

        in.close();
        client.close();

    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket client = socket.accept();
                //3、获得请求信息，解析HTTP协议内容
                process(client);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            flag = false;
        }
    }
}
