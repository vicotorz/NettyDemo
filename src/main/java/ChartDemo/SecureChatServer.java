package ChartDemo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetSocketAddress;

/**
 * Created by dell on 2019/5/8.
 */
public class SecureChatServer extends ChatServer {
    private final SslContext context;

    public SecureChatServer(SslContext context){
        this.context = context;
    }

    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group){
        return new SecureChatServerInitializer(group,context);
    }

    public static void main(String[] args) throws Exception{
        if(args.length!=1){
            System.out.println("Please give port as argument");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);


        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext context = SslContext.newServerContext(cert.certificate(),cert.privateKey());

        final SecureChatServer endpoint = new SecureChatServer(context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                endpoint.destroy();
            }
        });

        future.channel().closeFuture().syncUninterruptibly();
    }
}
