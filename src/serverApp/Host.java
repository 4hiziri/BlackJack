package serverApp;

public class Host extends gameServer.Player {

	public Host(acceptanceServer.Client client) {
		super(client);
	}
	
	@Override	
	public void receiveCard(gameServer.Card card){
		this.hand.add(card);					
		return;
	}
	
	@Override
	public void systemMessage(String msg){		
		return;
	}
}
