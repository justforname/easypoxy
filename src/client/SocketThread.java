package client;

import unit.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketThread extends Thread{
	private Socket socketIn;
    private InputStream isIn;
    private OutputStream osIn;
    private Socket socketOut;
    private InputStream isOut;
    private OutputStream osOut;
    private  SocThread outs;
    private static int hosy_type = 0;
    private byte[] buffer = new byte[4096];
    private static final byte[] VER = { 0x5, 0x0 };
    private static final byte[] CONNECT_OK = { 0x5, 0x0, 0x0, 0x1, 0, 0, 0, 0, 0, 0 };

    public SocketThread(Socket socket) {
        this.socketIn = socket;
    }
    public void run() {
        try {
            System.out.println("\n\na client connect " + socketIn.getInetAddress() + ":" + socketIn.getPort());
            isIn = socketIn.getInputStream();
            osIn = socketIn.getOutputStream();
            int len = isIn.read(buffer);
            System.out.println("< " + bytesToHexString(buffer, 0, len));
            osIn.write(VER);
            osIn.flush();
            len = isIn.read(buffer);
            System.out.println("> " + bytesToHexString(VER, 0, VER.length));
            System.out.println("< " + bytesToHexString(buffer, 0, len));

            String host = findvHost(buffer);
            int port = findvPort(buffer);
            byte[] host_byte = host.getBytes();
            System.out.println("host=" + host + ",port=" + port+" hostbyte="+ tools.bytesToHexString(host_byte,0,host_byte.length));

            //socketOut = new Socket(host, port);
            //isOut = socketOut.getInputStream();
            //osOut = socketOut.getOutputStream();

            for (int i = 4; i <= 9; i++) {
                CONNECT_OK[i] = buffer[i];
            }
            osIn.write(CONNECT_OK);
            osIn.flush();
            System.out.println("> " + bytesToHexString(CONNECT_OK, 0, CONNECT_OK.length));
            
            outs = new SocThread(isIn,osIn,host,port,hosy_type);

            outs.start();
            outs.join();
            
//            SocketThreadOutput out = new SocketThreadOutput(isIn, osOut);
//            out.start();
//            SocketThreadInput in = new SocketThreadInput(isOut, osIn);
//            in.start();
//            out.join();
//            in.join();
            
        } catch (Exception e) {
            System.out.println("a client leave");
        } finally {
            try {
                if (socketIn != null) {
                    socketIn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("socket close");
    }

    public static String findvHost(byte[] bArray){
    	StringBuffer sb = new StringBuffer();
    	if(3==bArray[3]){
            //域名类型
            hosy_type = 1;
    		String len = Integer.toString(0xFF & bArray[4]);
    		int ilen = Integer.parseInt(len,10);
    		for(int i=5;i<ilen+5;i++){
    			sb.append(asciiToString(Integer.toString(0xFF & bArray[i])));
    		}
    		return sb.toString();
    	}else if(1==bArray[3]){
            //IP类型
    		return findHost(bArray,4,7);
    	}else{
    		return null;
    	}
    	
    }
    
    public static int findvPort(byte[] bArray){
    	StringBuffer sb = new StringBuffer();
    	String temp;
    	if(3==bArray[3]){
    		String len = Integer.toString(0xFF & bArray[4]);
    		int ilen = Integer.parseInt(len,10);
    		for(int i=ilen+5;i<=ilen+6;i++){
    			temp = Integer.toHexString(0xFF & bArray[i]);
    			if(temp.length()<2)
    				sb.append(0);
    			sb.append(temp.toUpperCase());
    		}
    		return Integer.parseInt(sb.toString(),16);
    	}else if (1==bArray[3]) {
    		for(int i=8;i<=9;i++){
    			temp = Integer.toHexString(0xFF & bArray[i]);
    			if(temp.length()<2)
    				sb.append(0);
    			sb.append(temp.toUpperCase());
    		}
    		return Integer.parseInt(sb.toString(),16);
		}else
    	return 0;
    	
    }
    
    public static String findHost(byte[] bArray, int begin, int end) {
        StringBuffer sb = new StringBuffer();
        for (int i = begin; i <= end; i++) {
            sb.append(Integer.toString(0xFF & bArray[i]));
            sb.append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

//    public static int findPort(byte[] bArray, int begin, int end) {
//        int port = 0;
//        for (int i = begin; i <= end; i++) {
//            port <<= 16;
//            port += bArray[i];
//        }
//        return port;
//    }

    public static String asciiToString(String value){
         StringBuffer sbu = new StringBuffer();
         String[] chars = value.split(",");
         for (int i = 0; i < chars.length; i++) {
             sbu.append((char) Integer.parseInt(chars[i]));
         }
         return sbu.toString();
     }   

    // 4A 7D EB 69
    // 74 125 235 105
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
