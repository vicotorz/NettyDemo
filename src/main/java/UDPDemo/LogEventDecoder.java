package UDPDemo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;


import java.util.List;

/**
 * Created by dell on 2019/5/19.
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    protected void decode(ChannelHandlerContext ctx,
                          DatagramPacket datagramPacket,
                          List<Object> out) throws Exception {
        ByteBuf data = datagramPacket.content();
        int idx = data.indexOf(0,data.readableBytes(),LogEvent.SEPARATOR);
        String filename = data.slice(0,idx).toString(CharsetUtil.UTF_8);//文件名
        String logMsg = data.slice(idx+1,data.readableBytes()).toString(CharsetUtil.UTF_8);//提取日志消息

        LogEvent event = new LogEvent(datagramPacket.sender(),System.currentTimeMillis(),filename,logMsg);
        out.add(event);
    }
}
