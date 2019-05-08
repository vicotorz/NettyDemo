package ChartDemo;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by dell on 2019/5/8.
 *
 * 管理Http响应，处理Http请求
 */
public class HTTPRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HTTPRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();

        try{
            String path = location.toURI()+"index.html";
            path = !path.contains("file:")?path:path.substring(5);
            INDEX = new File(path);
        }catch (URISyntaxException e){
            throw new IllegalArgumentException("Unable to locate index.html",e);
        }
    }

    public HTTPRequestHandler(String wsUri){
        this.wsUri = wsUri;
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        if(wsUri.equalsIgnoreCase(request.getUri())){
            ctx.fireChannelRead(request.retain());
        }else{
            if(HttpHeaders.is100ContinueExpected(request)){
                send100Continue(ctx);
            }

            RandomAccessFile file = new RandomAccessFile(INDEX,"r");
            HttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"/text/plain;charset=UTF-8");

            boolean keepAlive = HttpHeaders.isKeepAlive(request);

            if(keepAlive){
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
            }

            ctx.write(response);

            if(ctx.pipeline().get(SslHandler.class) == null){
                ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));
            }else{
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if(!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }

    }

    private static void send100Continue(ChannelHandlerContext ctx){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        cause.printStackTrace();
        ctx.close();
    }


}
