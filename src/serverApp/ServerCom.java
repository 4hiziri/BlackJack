package serverApp;

import java.io.IOException;
import java.net.Socket;

public class ServerCom {
	private static final String address = ConnectionSetting.SERVER_HOST_ADDRESS;
	private static final int port = ConnectionSetting.SERVER_HOST_PORT;
	
	public static acceptanceServer.Client getConnection(){
		acceptanceServer.Client client = null;
		while(true){
			try{
				client = new acceptanceServer.Client(new Socket(address, port));
			} catch(IOException e) {
				System.out.println("cannot connect, try again...");
				e.printStackTrace();
				continue;			
			}
			
			try{
				client.openStream();
				break;
			} catch(IOException e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return client;
	}
}
