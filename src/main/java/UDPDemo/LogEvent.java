package UDPDemo;

import java.net.InetSocketAddress;

/**
 * Created by dell on 2019/5/15.
 * 消息当作事件处理,logEvent为日志事件
 *
 * logEvent --> LogEventEncoder --> 转换成DatagramPacket消息，并通过UDP进行广播
 */
public final class LogEvent {
    public static final byte SEPARATOR =(byte)':';
    private final InetSocketAddress source;
    private final String logfile;
    private final String msg;
    private final long received;

    public LogEvent(String logfile,String msg){
        this(null,-1,logfile,msg);
    }

    public LogEvent(InetSocketAddress source,long received,String logfile,String msg){
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource(){
        return source;
    }

    public String getLogfile(){
        return logfile;
    }

    public String getMsg(){
        return msg;
    }

    public long getReceivedTimestamp(){
        return received;
    }
}
