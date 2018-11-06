package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import server.CryThreadDe;
import server.CryThreadEn;
import server.Scry;
import unit.Sconfig;
import unit.tools;


public class RemoteThread {
    public static void main(String[] args) throws Exception{
//    	InputStream ii = null;
//    	OutputStream oo = null;
//    	SocThread soc =new SocThread(ii,oo,"199.32.12.90",299);
//    	soc.start();
//    	soc.join();
    	
    }
}


class SocThread extends Thread{
	private Socket socketIn;
	private InputStream isIn;
	private OutputStream osIn;
	private Socket socketOut;
    private InputStream isOut;
    private OutputStream osOut;
    private String host;
    private Integer port;
	private int hosy_type;
	private byte[] buffer = new byte[4096];
	private String user_key ;
	
	public SocThread(InputStream isIn,OutputStream osIn,String host,int port,int hosy_type){
		this.isIn = isIn;
		this.osIn = osIn;
		this.host = host;
		this.port = port;
		this.hosy_type = hosy_type;
	}
	
    private static final byte[] key = { 0x05, 0x00, 0x01, 0x09 };
    
	public void run(){
		try {
			System.out.println("\n\na connect to remote server...");

			String remote_host;
			int remote_port;
			remote_port = Sconfig.REMOTE_PORT;
			remote_host = Sconfig.REMOTE_IP;
			user_key = Sconfig.CONNECT_KEY;
			long time = System.currentTimeMillis();

			//链接端口号转换byte
			String port_16 = Integer.toHexString(port);
			if(port_16.length()<4){
	    		StringBuilder sb = new StringBuilder(port_16);
	    		for(int x=0;x<4-port_16.length();x++)
	    			sb.insert(0, "0");
	    		port_16 = sb.toString();
			}
			byte[] data_port = tools.str16tobyte(port_16);

			//生成校验key
			//osOut.write(byteMerger(byteMerger(key,data2),tools.MD5_16bit(userkey+time).getBytes()));
			byte[] k = tools.MD5_16bit(user_key+time).getBytes();

			byte[] tape = {0x01,0x00},data;
			//链接判断域名与IP
			if(hosy_type==0){
				//是ip地址
				String Ip_16= getIp(host);
				byte[] data_host = tools.str16tobyte(Ip_16);
				//System.out.println("-----IP----- "+Ip_16);
				data = byteMerger(tape,byteMerger(data_host,data_port));
				//System.out.println("-----IP_byte-----"+tools.bytesToHexString(data_host,0,data_host.length));
			}else{
				//是域名
				byte[] host_byte = host.getBytes();
				int len = 0xff & host_byte.length;
				tape[0] = 0x02;
				tape[1] = (byte)len;
				data = byteMerger(tape,byteMerger(host_byte,data_port));
			}
			//链接IP和端口加密
			byte[] k_data = byteMerger(k,data);
			byte[] en_data = Scry.Encrypt(k_data, user_key);
			//byte[] decode = Scry.Decrypt(kdat, userkey);
			//System.out.println(tools.bytesToHexString(kdata, 0, kdata.length));
			//System.out.println(tools.bytesToHexString(kdat, 0, kdat.length));
			//System.out.println(tools.bytesToHexString(decode, 0, decode.length));

			//链接到远程服务器
			socketOut = new Socket(remote_host, remote_port);
			isOut = socketOut.getInputStream();
			osOut = socketOut.getOutputStream();
			System.out.println(">server "+tools.bytesToHexString(en_data, 0, en_data.length));
			//发送加密的链接数据
			osOut.write(en_data);
			osOut.flush();
			int isl = isOut.read(buffer);
			System.out.println("<reserver " + tools.bytesToHexString(buffer, 0, isl));
			byte[] m = {0x00};

//          SocketThreadOutput out = new SocketThreadOutput(isIn, osOut);
//          out.start();
//          SocketThreadInput in = new SocketThreadInput(isOut, osIn);
//          in.start();
//          out.join();
//          in.join();

			//数据流处理
			CryThreadEn out = new CryThreadEn(isIn, osOut,m,user_key);
            out.start();
            CryThreadDe in = new CryThreadDe(isOut, osIn,m,user_key);
            in.start();
            out.join();
            in.join();
	        
		} catch (Exception e) {
			System.out.println("close a remote socket");
			socketOut=null;
		}finally {
			try {
				if(socketOut!=null){
					socketOut.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
		System.out.println("remote socket close");
	}

	public void rSocketclose(){
		try {
			if(socketOut!=null){
				socketOut.close();
			}
		}catch (Exception e){

		}
	}

	//ip 转16进制
	public static String getIp(String host){

		String[] vhost = host.split("\\.");
		StringBuilder host_16 = new StringBuilder();
		for(int i=0;i<vhost.length;i++){
			int tmp = Integer.parseInt(vhost[i]);
			String t = Integer.toHexString(tmp);
			if(t.length()<2)
				host_16.append(0);
			host_16.append(t);
		}
		return host_16.toString();
	}

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    } 

    
    public static String str2HexStr(String str){
        char[] chars = "0123456789ABCDEF".toCharArray();      
        StringBuilder sb = new StringBuilder("");    
        byte[] bs = str.getBytes();      
        int bit;      
            
        for (int i = 0; i < bs.length; i++)    
        {      
            bit = (bs[i] & 0x0f0) >> 4;      
            sb.append(chars[bit]);      
            bit = bs[i] & 0x0f;      
            sb.append(chars[bit]);    
            sb.append(' ');    
        }      
        return sb.toString().trim();      
    }
    
    public static String hexStr2Str(String hexStr){      
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
      
        for (int i = 0; i < bytes.length; i++){
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
}