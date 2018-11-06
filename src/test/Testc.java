package test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.codec.binary.Base64;

import server.Scry;
import unit.tools;

public class Testc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			ClentT ct = new ClentT();
			ct.start();
			ct.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}


class ClentT extends Thread{
	
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private byte[] buffer = new byte[4096];
	
	public ClentT(){
		
		
	}
	
	public void run(){
		try {
			socket = new Socket("127.0.0.1", 8910);
			in = socket.getInputStream();
			out = socket.getOutputStream();
			String key = "1234567890";
			File file = new File("D://test_mix_audio_speak_mix.pcm");
			FileInputStream fis = new FileInputStream(file);
			
			byte[] data2 = {1,2,3,4,5,5,6,7,7,8,8,6,5,4,4,3,3,3,3,3,3,4,4,5,5,6,6,7,7,12,3,4,1,41,5,15,16,1,6};

			int il, i=0,sum=0;
			while((il = fis.read(buffer))!=-1){
				byte[] x = Scry.Encrypt(tools.subBytes(buffer, 0, il), key);
				i++;
				System.out.println("SE LEN:"+i+"_"+x.length);
				sum+=il;
				//Thread.sleep(50);
				out.write(x);
				out.flush();
				
			}
			System.out.println("All_"+sum);
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}