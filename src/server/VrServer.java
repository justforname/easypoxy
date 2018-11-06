package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import client.SocketThreadInput;
import client.SocketThreadOutput;
import unit.Sconfig;
import unit.tools;

public class VrServer {
	public static void mainrun() throws Exception{
        // TODO Auto-generated method stub
		//服务端
		ServerSocket sserver_Socket = new ServerSocket(Sconfig.SER_PROXY_PORT);
		while (true) {
			Socket socket = null;
			try {
				socket = sserver_Socket.accept();
				new SosThread(socket).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}


class SosThread extends Thread{
	
	private Socket socketIn;
	private InputStream isIn;
	private OutputStream osIn;
	private Socket socketOut;
    private InputStream isOut;
    private OutputStream osOut;
    private String rhost;
    private int rport;
	private byte[] buffer = new byte[4096];
	
	private String user_key ;

	public SosThread(Socket socket) {
		this.socketIn = socket;
	}
	
	public void run(){
		try {
            user_key = Sconfig.CONNECT_KEY;
			System.out.println("\n\na connect from " + socketIn.getInetAddress() + ":" + socketIn.getPort());
	        isIn = socketIn.getInputStream();
	        osIn = socketIn.getOutputStream();
			//DataInputStream dis = new DataInputStream(isIn);
			//dis.readFully(buffer);
	        int len = isIn.read(buffer);
	        //byte[] x = {0x00,0x02};
	        //System.out.println(tools.MD5_16bit(x));
	        System.out.println("< "+bytesToHexString(buffer,0,len));

	        byte[] decode = Scry.Decrypt(subBytes(buffer,0,len), user_key);
	        
	        System.out.println("----SERVERDECODE----"+bytesToHexString(decode,0,decode.length));
	        byte[] ipd = subBytes(decode,16,decode.length-16);
	        rhost = findremotehost(ipd);
	        rport = findremoteport(ipd);
	        
	        System.out.println("remotehost="+rhost+"---remoteport="+rport);
			long time = System.currentTimeMillis();

	        osIn.write(tools.MD5_16bit(time+user_key).getBytes());
	        osIn.flush();
	        
	        socketOut = new Socket(rhost, rport);
            isOut = socketOut.getInputStream();
            osOut = socketOut.getOutputStream();

//          SocketThreadOutput out = new SocketThreadOutput(isIn, osOut);
//          out.start();
//          SocketThreadInput in = new SocketThreadInput(isOut, osIn);
//          in.start();
//          out.join();
//          in.join();

            byte[] m = {0x00};
            CryThreadDe out = new CryThreadDe(isIn, osOut, m, user_key);
            out.start();
            CryThreadEn in = new CryThreadEn(isOut, osIn, m, user_key);
            in.start();
            out.join();
            in.join();
	        
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
            try {
                if (socketIn != null) {
					socketIn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("remote server socket close");

		
	}
	
	public String findremotehost(byte[] bArray){
		StringBuffer sb = new StringBuffer();
		if(1==bArray[0]){
			for (int i = 2; i < 6; i++) {
	            sb.append(Integer.toString(0xFF & bArray[i]));
	            sb.append(".");
	        }
	        sb.deleteCharAt(sb.length() - 1);
	        return sb.toString();
		}else if(2==bArray[0]){
    		String len = Integer.toString(0xFF & bArray[1]);
    		int ilen = Integer.parseInt(len,10);
			for (int i = 2; i < ilen+2; i++) {
				sb.append(tools.asciiToString(Integer.toString(0xFF & bArray[i])));
	        }
			return sb.toString();
		}else{
			 return null;
		}
		 
	}
	
	public int findremoteport(byte[] bArray){
		StringBuffer sb = new StringBuffer();
		String temp;
		if(1==bArray[0]){
			for(int i=6;i<8;i++){
				temp = Integer.toHexString(0xFF & bArray[i]);
				if(temp.length()<2)
					sb.append(0);
				sb.append(temp.toUpperCase());
			}
			return Integer.parseInt(sb.toString(),10);
		}else if(2==bArray[0]){
			String len = Integer.toString(0xFF & bArray[1]);
    		int ilen = Integer.parseInt(len,10);
			for(int i=ilen+2;i<ilen+4;i++){
				temp = Integer.toHexString(0xFF & bArray[i]);
				if(temp.length()<2)
					sb.append(0);
				sb.append(temp.toUpperCase());
			}
			return Integer.parseInt(sb.toString(),16);
		}else{
			return 0;
		}
		
	}
	
	public String getKey(byte[] bArray){
		StringBuffer sb = new StringBuffer();
		String tmp;
		for(int i=1;i<17;i++){
			tmp = Integer.toHexString(0xFF & bArray[i]);
			if(tmp.length() < 2)
				sb.append(0);
			sb.append(tmp.toUpperCase());
		}
		return sb.toString();
	}
	
	public static byte[] subBytes(byte[] src, int begin, int count) {
		
	    byte[] bs = new byte[count];  
	    System.arraycopy(src, begin, bs, 0, count);  
	    return bs;  
	}  
    
	public static final String bytesToHexString(byte[] bArray, int begin, int end) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = begin; i < end; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }
	
}