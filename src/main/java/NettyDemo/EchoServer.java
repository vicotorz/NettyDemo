package NettyDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by dell on 2019/4/19.
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage:" + EchoServer.class.getSimpleName() + "<port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();//Hnadler处理逻辑
        EventLoopGroup group = new NioEventLoopGroup();//EventLoop组
        try {
            ServerBootstrap b = new ServerBootstrap();//引导
            //组装引导、EventLoopGroup、channel类型、地址、ChannelHandler调用链
            b.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(serverHandler);//pipeline: ChannelHandler链
                        }
                    });
            //引导执行
            ChannelFuture f = b.bind().sync();
            //结束
            f.channel().closeFuture().sync();
        } finally {
            //关闭EventLoop组
            group.shutdownGracefully().sync();
        }
    }
}
