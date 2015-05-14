package opdracht4;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MessageHandlerBehaviour extends CyclicBehaviour{

	private AgentA agent;
	
	MessageHandlerBehaviour(AgentA agent){
		this.agent = agent;
	}
	@Override
	public void action() {
		// TODO Auto-generated method stub
		ACLMessage  msg = agent.receive();
		if (msg != null){ 
			switch (msg.getPerformative()) {
			// messagetype holding the requested state for the equiplet
				case ACLMessage.PROPOSE:
					if(msg.getContent().equals(Constants.startGame)){
						System.out.println(Constants.startGame);
						acceptGame(msg);								
					}
					break;
				case ACLMessage.ACCEPT_PROPOSAL:
					if(msg.getContent().equals(Constants.acceptGame)){
						//this is not needed so don't do anything special here
					}
					if(agent.isPlaying()){
						ACLMessage reply = msg.createReply();
						reply.setPerformative( ACLMessage.REFUSE );
				        reply.setContent("Already playing");
				        agent.send(reply);
						break;
					}
					System.out.println("Accept game");
					
					agent.setOppositePlayer(msg.getSender());
					agent.startGame();

					break;
				case ACLMessage.INFORM:
					String language = msg.getLanguage();
					String content = msg.getContent();
					if(language.equals(Constants.meta)){
						switch(content){
							case Constants.introduce:{
								System.out.println(Constants.introduce);
								break;
							}
							case Constants.makeMove:{
								System.out.println(Constants.makeMove);
								break;
							}
							case Constants.congratz:{
								System.out.println(Constants.congratz);
								agent.signOff();
								break;
							}
							case Constants.signOff:{
								System.out.println(Constants.signOff);
								break;
							}
							default:{
								System.out.println("ELSE");
								System.out.println(msg.getContent());
							}
						}
					}else if(language.equals(Constants.game)){
						try{
							agent.setOppositePlayer(msg.getSender());
							FifteenStack stack = (FifteenStack) msg.getContentObject();
							agent.makeMove(stack);
						}catch(Exception ex){
							System.err.println("Game language but no fifteenstack object!");
							ex.printStackTrace();
						}
					}else{
						System.err.println("Unkown language");
					}
						
				default:
					break;
			}
		}
		block(); 
	}
	
	public void acceptGame(ACLMessage msg){
		//if(amountOfPlayers >= maxPlayers){
		ACLMessage reply = msg.createReply();
		msg.setLanguage(Constants.meta);
		if(agent.isPlaying()){
	        reply.setPerformative( ACLMessage.REFUSE );
	        reply.setContent("NO!");
	        agent.send(reply);
	        System.out.println("REFUSE");
		}else{
			reply.setPerformative( ACLMessage.ACCEPT_PROPOSAL);
			reply.setContent(Constants.acceptGame);
			agent.send(reply);
			System.out.println("ACCEPT GAME");
		}
	}

}
