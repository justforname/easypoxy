package test;

import java.awt.image.ImageProducer;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.codec.binary.Base64;

import client.SocketThread;
import server.Scry;
import unit.tools;

public class Tests {

	public static void main(String[] args) throws Throwable {
		// TODO Auto-generated method stub
		ServerSocket serverSocket = new ServerSocket(8910);
        while (true) { 
            Socket socket = null;  
            try {  
                socket = serverSocket.accept();  
                new ServerT(socket).start();
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }

	}

}


class ServerT extends Thread{
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	private byte[] buffer = new byte[4112];
	public ServerT(Socket socket){
		this.socket = socket;
		
	}
	
	public void run(){
		try {
			String key = "1234567890";
			in = socket.getInputStream();
			out = socket.getOutputStream();
			File file = new File("D://lllll.z");
			FileOutputStream fos = new FileOutputStream(file);
			int len,i=0,sum=0;

			BufferedInputStream bis = new BufferedInputStream(in,1024*5);
			DataInputStream is = new DataInputStream(in);
			while(true){
				//Thread.sleep(10);
				is.readFully(buffer);
				i++;
				System.out.println("RR LEN:"+i+"_");
				byte[] des = Scry.Decrypt(tools.subBytes(buffer, 0, buffer.length),key);
				fos.write(des, 0, des.length);
				fos.flush();
			}

			//fos.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}