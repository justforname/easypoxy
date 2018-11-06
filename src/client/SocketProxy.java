package client;

import unit.Sconfig;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketProxy {

    public static void mainrun() throws Exception {
        //运行代理程序
        ServerSocket serverSocket = new ServerSocket(Sconfig.LOC_PROXY_PORT);
        while (true) {
            Socket socket = null;
            try {  
                socket = serverSocket.accept();
                new SocketThread(socket).start();
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
