package UDPDemo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by dell on 2019/5/19.
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent>{

    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws  Exception{
        cause.printStackTrace();
        ctx.close();
    }

    protected void channelRead0(ChannelHandlerContext ctx,
                                LogEvent logEvent) throws Exception {
        StringBuffer builder = new StringBuffer();
        builder.append(logEvent.getReceivedTimestamp());
        builder.append("[");
        builder.append(logEvent.getSource().toString());
        builder.append("][");
        builder.append(logEvent.getLogfile());
        builder.append("]:");
        builder.append(logEvent.getMsg());
        System.out.println(builder.toString());
    }
}
