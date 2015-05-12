package opdracht4;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;


public class AgentA extends jade.core.Agent{

	private static final long serialVersionUID = 2161863190929663356L;
	
	private static String introduce = "introduceren";
	private static String startGame = "spel starten";
	private static String acceptGame = "Come at me brawh";
	private static String makeMove = "spelbeurt";
	private static String congratz = "gefeliciteerd";
	private static String logout = "afmelden";
	
	private static String meta = "meta";
	private static String game = "game";
	
	private static String playerName = null;
	
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
		
		//playerName = args[0].toString();

		for(int i = 0; i < args.length; i++){
			String agentName = args[i].toString();
			playerName = args[i].toString();
			message.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			send(message);
			
			messageStart.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			send(messageStart);
		}
		System.out.println(playerName);
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
								isPlaying = true;
								//player = 2;
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
							//makeMove();
							break;
						case ACLMessage.INFORM:
							//System.out.println("Bericht!");
							if(msg.getContent().equals(introduce)){
							
							}else if(msg.getContent().equals(makeMove)){
								
							}else if(msg.getContent().equals(congratz)){
								
							}else if(msg.getContent().equals(logout)){
								
							}else {
								try{
									FifteenStack stack = (FifteenStack) msg.getContentObject();
									makeMove(stack);
								}catch(Exception ex){
									System.out.println("Oeps!");
									ex.printStackTrace();
									System.out.println('\n');
								}
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
	        send(reply);
		}else{
			reply.setPerformative( ACLMessage.ACCEPT_PROPOSAL);
			reply.setContent(acceptGame);
			//player = 1;
			isPlaying = true;
			FifteenStack stack = new FifteenStack();
			send(reply);
			makeMove(stack);
		}
	}
	
	public void makeMove(FifteenStack stack){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		//message.addReceiver(oppositePlayer);
		message.addReceiver(new AID(playerName, AID.ISLOCALNAME));
		
		message.setLanguage(game);	
		if(stack.look(1) > 0){
			stack.take(1, 1);
			System.out.println(getLocalName());
			System.out.println(stack.toString());
			//message.setContent(stack.toString());
			try{
				message.setContentObject(stack);
			} catch(Exception ex) {
				System.out.println("Catch");
				ex.printStackTrace();
			}
			//System.out.println("Send");
			send(message);
		}
		
		//message.setContent(content);
	}
}
 