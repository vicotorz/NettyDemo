package ChartDemo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by dell on 2019/5/8.
 * 初始化Pipeline
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group){
        this.group = group;
    }

    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec());//将HttpRequest，HttpContent，LastHttpContent编码为字节
        pipeline.addLast(new ChunkedWriteHandler());//写入文件的内容
        pipeline.addLast(new HttpObjectAggregator(64*1024));//将一个HttpMessage和跟随它的多个HttpContent聚合为单个FullHttpRequest
        pipeline.addLast(new HTTPRequestHandler("/ws"));//处理FullHttpRequest
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));//处理WebSocket升级握手,PingWebSocketFrame,PongWebSocketFrame和CloseWebSocketFrame
        pipeline.addLast(new TextWebSocketFrameHandler(group));//处理TextWebSocketFrame和握手完成事件
    }
}
