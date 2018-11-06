package server;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

import unit.tools;

public class CryThreadEn extends Thread{
	
	private InputStream In;
    private OutputStream Out;
    private byte[] ktag;
    private byte[] buffer = new byte[40960];
    private String key = "";
    
	public CryThreadEn(InputStream In, OutputStream Out, byte[] ktag, String key){
        this.In = In;
        this.Out = Out;
        this.ktag = ktag;
        this.key = key;
		
	}
	
	public void run(){
		try {
            int len;
            while ((len = In.read(buffer)) != -1) {
                long time = System.currentTimeMillis();
                byte[] tk = tools.MD5_16bit(String.valueOf(time)).getBytes();
                //System.out.println("MD5------"+tools.bytesToHexString(tk,0,tk.length));
                byte[] sum = tools.byteMerger(tk,tools.subBytes(buffer, 0, len));
                byte[] tmp = Scry.Encrypt(sum, key);
                String leng_16 = Integer.toHexString(tmp.length);
                if(leng_16.length()<4){
                    StringBuilder sb = new StringBuilder(leng_16);
                    for(int x=0;x<4-leng_16.length();x++)
                        sb.insert(0, "0");
                    leng_16 = sb.toString();
                }
                byte[]  len_byte= tools.str16tobyte(leng_16);
                byte[] ttle = tools.byteMerger(len_byte,tmp);
                //System.out.println("-----ENLen-----="+leng_16);
                Out.write(ttle);
                Out.flush();
            }
        } catch (Exception e) {
            System.out.println("SocketThreadOutput leave");  
        }
		
	}


}
