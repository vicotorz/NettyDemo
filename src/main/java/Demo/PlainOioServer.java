package Demo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * 未使用Netty的阻塞
 * Created by dell on 2019/4/24.
 */
public class PlainOioServer {
    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);
        try{
            for(;;){
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from"+clientSocket);

                new Thread(new Runnable() {
                    public void run() {
                        OutputStream out;
                        try{
                            out = clientSocket.getOutputStream();
                            out.write("hello".getBytes());
                            out.flush();
                            clientSocket.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally{
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
