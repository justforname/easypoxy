package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Scry {

	//加密
    public static byte[] Encrypt(byte[] content, String sKey) throws Exception {
       
        try {
        	 if (sKey == null) {
                 System.out.print("Key is null");
                 return null;
             }
        	 
        	 String iv = "abcdefghijklmnop";
             byte[] keyBytes = sKey.getBytes();
             KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keyBytes);
             keyGenerator.init(128, secureRandom);
             SecretKey key=keyGenerator.generateKey();
             Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
             cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv.getBytes()));
             byte[] result=cipher.doFinal(content);
             return result;
             
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
       
        
    }

    //解密
    public static byte[] Decrypt(byte[] content, String sKey) throws Exception {
        try {
            if (sKey == null) {
                System.out.print("Key is null");
                return null;
            }
            String iv = "abcdefghijklmnop";
            byte[] keyBytes = sKey.getBytes();
            KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keyBytes);
            keyGenerator.init(128, secureRandom);//key 128 192 256 128
            SecretKey key=keyGenerator.generateKey();
            String x = "PKCS5Padding";
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv.getBytes()));  
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    
 
    //流加密
    public static CipherInputStream EncryptStream(InputStream content, String sKey) throws Exception {
        
        try {
        	 if (sKey == null) {
                 System.out.print("Key is null");
                 return null;
             }
        	 ;
        	 String iv = "abcdefghijklmnop";
             byte[] keyBytes = sKey.getBytes();
             KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
             keyGenerator.init(128, new SecureRandom(keyBytes));
             SecretKey key=keyGenerator.generateKey();
             Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
             cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv.getBytes()));
             //byte[] result=cipher.doFinal(content);
             CipherInputStream cin = new CipherInputStream(content, cipher);
             return cin; 
             
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
       
        
    }

    // 流解密
    public static CipherOutputStream DecryptStream(OutputStream content, String sKey) throws Exception {
        try {
            if (sKey == null) {
                System.out.print("Key is null");
                return null;
            }
            String iv = "abcdefghijklmnop";
            byte[] keyBytes = sKey.getBytes();
            KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom(keyBytes));//key 128 192 256 128
            SecretKey key=keyGenerator.generateKey();  
            Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");  
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv.getBytes()));  
            //byte[] result=cipher.doFinal(content);
            CipherOutputStream cin = new CipherOutputStream(content, cipher);
            return cin;
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    
    
    
}
