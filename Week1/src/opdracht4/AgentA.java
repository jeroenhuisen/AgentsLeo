package opdracht4;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentA extends jade.core.Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2161863190929663356L;
	
	private static String introduce = "introduceren";
	private static String startGame = "spel starten";
	private static String acceptGame = "Come at me brawh";
	private static String makeMove = "spelbeurt";
	private static String congratz = "gefeliciteerd";
	private static String logout = "afmelden";
	
	private static String meta = "meta";
	private static String game = "game";
	
	private AID oppositePlayer;
	
	//private int amountOfPlayers = 0;
	//private static int maxPlayers = 10;
	private boolean isPlaying = false;

	protected void setup(){
		Object[] args = getArguments();
		
		System.out.println("Agent: " + getLocalName() + " started");
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.setContent(introduce);
		message.setLanguage(meta);
		
		ACLMessage messageStart = new ACLMessage(ACLMessage.PROPOSE);
		messageStart.setContent(startGame);
		messageStart.setLanguage(meta);
		
		for(int i = 0; i < args.length; i++){
			String agentName = args[i].toString();
			
			message.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			send(message);
			
			messageStart.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			send(messageStart);
		}
		
		addBehaviour(new CyclicBehaviour(){
			private static final long serialVersionUID = 1L;
			
			public void action(){ 
				ACLMessage msg = receive();
				if (msg != null){ 
					switch (msg.getPerformative()) {
					// messagetype holding the requested state for the equiplet
						case ACLMessage.PROPOSE:
							if(msg.getContent().equals(introduce)){
								
							}else if(msg.getContent().equals(startGame)){
								startGame(msg);								
							}
							break;
						case ACLMessage.ACCEPT_PROPOSAL:
							if(msg.getContent().equals(acceptGame)){
								//this is not needed so don't do anything special here
							}
							if(isPlaying){
								ACLMessage reply = msg.createReply();
								reply.setPerformative( ACLMessage.REFUSE );
						        reply.setContent("Already playing");
						        send(reply);
								break;
							}
							
							oppositePlayer = msg.getSender();
							makeMove();
							break;
						case ACLMessage.INFORM:
							if(msg.getContent().equals(makeMove)){
								
							}else if(msg.getContent().equals(congratz)){
								
							}else if(msg.getContent().equals(logout)){
								
							}
						default:
							break;
					}
					/*System.out.println(msg.getContent());	
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.addReceiver(msg.getSender());
					message.setContent("Hello!");
					send(message);*/
				}
				block(); 
			}
		});
		
		
	}
	public void startGame(ACLMessage msg){
		//if(amountOfPlayers >= maxPlayers){
		ACLMessage reply = msg.createReply();
		msg.setLanguage(meta);
		if(isPlaying){
	        reply.setPerformative( ACLMessage.REFUSE );
	        reply.setContent("NO!");
		}else{
			reply.setPerformative( ACLMessage.ACCEPT_PROPOSAL);
			reply.setContent(acceptGame);
		}
		send(reply);
	}
	
	public void makeMove(){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(oppositePlayer);
		message.setLanguage(game);
		//message.setContent(content);
	}

}
 