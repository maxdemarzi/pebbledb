package com.pebbledb.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.BufferedInputStream;

public class SwaggerUIHandler implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        // Please consider run blocking operations on Undertow's Worker Threads, instead of Undertow's IO Thread.
        // Even under mid-level load on your server, you could face some IO issues if you don't do it.
        // Using Undertow's API, one should always rely on the following pattern:
        if ( !exchange.isInIoThread() ){
            exchange.dispatch( this );
            return;
        }

        //String html = "resources" + exchange.getRequestPath();
        String html = exchange.getRequestPath();
        html = html.substring(1,html.length() );
        //webjars/swagger-ui/favicon-32x32.png

//        Reflections reflections = new Reflections(null, new ResourcesScanner());
//        Set<String> resourceList = reflections.getResources(x -> true);
//        for (String resource : resourceList) {
//            if (resource.contains("swagger")) {
//                System.out.println(resource);
//            }
//        }
        //System.out.println(resourceList);
        try {
            if(html.endsWith(".png")) {
                exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "image/png");
            } else {
                exchange.getResponseHeaders().add(Headers.CONTENT_ENCODING, Headers.GZIP.toString());
               // html += ".gz";

                if (html.endsWith(".css")) {
                    exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/css");
                } else if (html.endsWith(".js")) {
                    exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/javascript");
                } else if (html.endsWith(".html")) {
                    exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html");
                }
            }

            // Allocate 5 kilobytes for index.html
            ByteBufferOutputStream bbos = new ByteBufferOutputStream(5*1024);


            BufferedInputStream in = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream(html));
            //BufferedInputStream in = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("webjars/swagger-ui/index.html"));

            //BufferedInputStream in = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("META-INF/resources/webjars/swagger-ui/3.1.7"));
            byte[] buff = new byte[5*1024]; //or some size, can try out different sizes for performance

            int n = 0;
            while ((n = in.read(buff)) >= 0) {
                bbos.write(buff, 0, n);
            }
            in.close();
            bbos.close();

            exchange.getResponseSender().send(bbos.getByteBuffer());
        } catch (Exception e) {
            System.out.println("error in: " + html);
            e.printStackTrace();
        }
        }
}
