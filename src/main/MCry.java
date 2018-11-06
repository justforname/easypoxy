package main;

import client.SocketProxy;
import server.VrServer;
import unit.Sconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MCry {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Properties conf;
		if(args.length==2){
			switch (args[0]){
				case "-c":

					conf = getConfig(args[1]);
					Sconfig.LOC_PROXY_PORT = Integer.parseInt(conf.getProperty("LOC_PROXY_PORT"));
					Sconfig.REMOTE_IP = conf.getProperty("REMOTE_IP");
					Sconfig.REMOTE_PORT = Integer.parseInt(conf.getProperty("REMOTE_PORT"));
					Sconfig.CONNECT_KEY = conf.getProperty("CONNECT_KEY");
					SocketProxy sp = new SocketProxy();
					System.out.println("Client running!\r\n Socks5 LOC_PROXY_PORT:"+Sconfig.LOC_PROXY_PORT+"\r\n REMOTE_IP:"+Sconfig.REMOTE_IP+"\r\n REMOTE_PORT:"+Sconfig.REMOTE_PORT+
							"\r\n CONNECT_KEY:"+Sconfig.CONNECT_KEY);
					sp.mainrun();
					break;
				case "-s":
					conf = getConfig(args[1]);
					Sconfig.SER_PROXY_PORT = Integer.parseInt(conf.getProperty("SER_PROXY_PORT"));
					Sconfig.CONNECT_KEY = conf.getProperty("CONNECT_KEY");
					VrServer vs = new VrServer();
					System.out.println("Server running!\r\n SER_PROXY_PORT:"+Sconfig.SER_PROXY_PORT+"\r\n CONNECT_KEY:"+Sconfig.CONNECT_KEY);
					vs.mainrun();
					break;
				default:
					System.out.println("RcyTCPProxy. Socks5.\r\n Input error! \"-c\": client mode \"-s\": server mode\r\n Exemple: -c config.properties");
			}
		}else {
			System.out.println("RcyTCPProxy. Socks5.\r\n Input error! \"-c\": client mode \"-s\": server mode\r\n Exemple: -c config.properties");
		}



	}

	public static Properties getConfig(String path){
		try {
			Properties prop = new Properties();
			File f = new File(path);
			InputStream is = new FileInputStream(f);
			prop.load(is);
			//prop.getProperty("");
			return prop;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

}
