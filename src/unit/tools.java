package unit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class tools {

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
    
    public static String asciiToString(String value){
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }  
    
	public static byte[] subBytes(byte[] src, int begin, int count) {
		
	    byte[] bs = new byte[count];  
	    System.arraycopy(src, begin, bs, 0, count);  
	    return bs;  
	}

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
    public static byte[] str16tobyte(String str){
        int line = str.length();
        if(line%2!=0){
            StringBuilder sb = new StringBuilder(str);
            sb.insert(0, "0");
            str = sb.toString();
        }
        byte[] a = new byte[str.length()/2];
        int s=0;
        for(int i=0;i<str.length()/2;i++){
            int x = Integer.parseInt(str.substring(s, s+2),16);
            s += 2;
            a[i] = (byte)x;
        }
        return a;
    }

    public static int byteArrayToInt(byte[] b){
        return b[1]&0xFF | (b[0]&0xFF) << 8;
    }

    public static final String MD5_16bit(String readyEncryptStr) throws NoSuchAlgorithmException{  
        if(readyEncryptStr != null){  
            return MD5_32bit(readyEncryptStr).substring(8, 24);  
        }else{  
            return null;  
        }  
    } 
	
    public static final String MD5_32bit(String readyEncryptStr) throws NoSuchAlgorithmException{  
        if(readyEncryptStr != null){  
            //Get MD5 digest algorithm's MessageDigest's instance.  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            //Use specified byte update digest.  
            md.update(readyEncryptStr.getBytes());  
            //Get cipher text  
            byte [] b = md.digest();  
            //The cipher text converted to hexadecimal string  
            StringBuilder su = new StringBuilder();  
            //byte array switch hexadecimal number.  
            for(int offset = 0,bLen = b.length; offset < bLen; offset++){  
                String haxHex = Integer.toHexString(b[offset] & 0xFF);  
                if(haxHex.length() < 2){  
                    su.append("0");  
                }  
                su.append(haxHex);  
            }  
            return su.toString();  
        }else{  
            return null;  
        }  
    }
}
