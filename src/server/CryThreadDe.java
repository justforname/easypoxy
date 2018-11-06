package server;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

import unit.tools;

public class CryThreadDe extends Thread{
	
	private InputStream In;
    private OutputStream Out;
    private byte[] ktag;
    private byte[] buffer = new byte[4112];
    private String key = "";
	
	public CryThreadDe(InputStream In, OutputStream Out, byte[] ktag, String key){
        this.In = In;
        this.Out = Out;
        this.ktag = ktag;
        this.key = key;
		
	}
    
	public void run(){
		try {
            int len;
            byte[] datalen = new byte[2];
            DataInputStream dis = new DataInputStream(In);
            while (true) {
                In.read(datalen);
                int length = tools.byteArrayToInt(datalen);
                if(length==0) break;
                //System.out.println("-----DELen-----="+tools.bytesToHexString(datalen,0,datalen.length));
                byte[] data = new byte[length];
                dis.readFully(data);
                //Thread.sleep(20);
                byte[] tmp = Scry.Decrypt(data, key);
                byte[] de = tools.subBytes(tmp,16,tmp.length-16);
                //System.out.println("DE="+buffer.length);
                Out.write(de);
                Out.flush();

            }
        } catch (Exception e) {
            System.out.println("SocketThreadOutput leave");  
        }
		
	}
}
