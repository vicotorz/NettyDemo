package Demo;

import io.netty.channel.ServerChannel;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by dell on 2019/4/24.
 */
public class PlainNioServer {
    public void serve(int port) throws Exception{
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket ssocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ssocket.bind(address);

        Selector selector = Selector.open();//selector
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);//serverChannel注册到selector
        final ByteBuffer msg = ByteBuffer.wrap("Hello".getBytes());
        for(;;){
            try{
                selector.select();//等待需要处理的新事件，阻塞将一直传入到下一个事件
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
            //获取所有接收事件的SelectionKey
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                try{
                    //检查接收事件的key是否已经接收
                    if(key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        //接收客户端，并将它注册到选择器
                        client.register(selector,SelectionKey.OP_WRITE|SelectionKey.OP_READ,msg.duplicate());
                        System.out.println("Accepted connection from"+client);
                    }

                    if(key.isWritable()){
                        SocketChannel client = (SocketChannel)key.channel();
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        while(buffer.hasRemaining()){
                            if(client.write(buffer) == 0){
                                break;
                            }
                        }
                        client.close();
                    }
                }catch (Exception e){
                    key.cancel();
                    key.channel().close();
                }
            }
        }

    }
}
