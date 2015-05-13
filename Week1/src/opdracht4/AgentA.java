package opdracht4;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;


public class AgentA extends jade.core.Agent{

	private static final long serialVersionUID = 2161863190929663356L;
	
	private final static String introduce = "introduceren";
	private final static String startGame = "spel starten";
	private final static String acceptGame = "Come at me brawh";
	private final static String makeMove = "spelbeurt";
	private final static String congratz = "gefeliciteerd";
	private final static String signOff = "afmelden";
	
	private final static String meta = "meta";
	private final static String game = "game";
	
	private String playstyle = ""; 
	
	private AID oppositePlayer = null;
	
	//private int amountOfPlayers = 0;
	//private static int maxPlayers = 10;
	private boolean isPlaying = false;
	
	private CalculatorHandler calculatorHandler = null;

	protected void setup(){
		Object[] args = getArguments();
		
		System.out.println("Agent: " + getLocalName() + " started");
		
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.setContent(introduce);
		message.setLanguage(meta);
		
		ACLMessage messageStart = new ACLMessage(ACLMessage.PROPOSE);
		messageStart.setContent(startGame);
		messageStart.setLanguage(meta);
		
		// first args should be the playstyle
		if(args.length > 0){
			playstyle = args[0].toString();
		}

		for(int i = 1; i < args.length; i++){
			String agentName = args[i].toString();
			message.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			send(message);
			
			messageStart.addReceiver(new AID(agentName, AID.ISLOCALNAME));
			send(messageStart);
		}
		addBehaviour(new CyclicBehaviour(){
			private static final long serialVersionUID = 1L;
			
			public void action(){ 
				ACLMessage  msg = receive();
				if (msg != null){ 
					switch (msg.getPerformative()) {
					// messagetype holding the requested state for the equiplet
						case ACLMessage.PROPOSE:
							if(msg.getContent().equals(startGame)){
								System.out.println(startGame);
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
							System.out.println("Accept game");
							isPlaying = true;
							oppositePlayer = msg.getSender();
							FifteenStack fs = new FifteenStack();
							makeMove(fs);
							break;
						case ACLMessage.INFORM:
							//System.out.println("Bericht!");
							if(msg.getContent().equals(introduce)){
								System.out.println(introduce);
							}else if(msg.getContent().equals(makeMove)){
								System.out.println(makeMove);
							}else if(msg.getContent().equals(congratz)){
								System.out.println(congratz);
								signOff();
							}else if(msg.getContent().equals(signOff)){
								System.out.println(signOff);
							}else {
								System.out.println("ELSE");
								System.out.println(msg.getContent());
								try{
									oppositePlayer = msg.getSender();
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
	        System.out.println("REFUSE");
		}else{
			reply.setPerformative( ACLMessage.ACCEPT_PROPOSAL);
			reply.setContent(acceptGame);
			send(reply);
			System.out.println("ACCEPT GAME");
		}
	}
	
	public void makeMove(FifteenStack stack){
		if(stack.gameOver()){
			String gameAlreadyOverMessage = "You lost, already game over and still send message? y u do dis? Cheater";
			rageMessage(gameAlreadyOverMessage);
			signOff();
		}
		System.out.println(stack.toString());
		
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(oppositePlayer);

		if(calculatorHandler == null){
			calculatorHandler = new CalculatorHandler(playstyle);
		}
		calculatorHandler.calculateStack(stack);
		message.setLanguage(game);	
		try{
			message.setContentObject(stack);
		} catch(Exception ex) {
			System.out.println("Catch");
			ex.printStackTrace();
		}
		
		
		if(stack.gameOver()){
			gameOver();
		}else{
			send(message);
		}
	}
	
	public void signOff(){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(oppositePlayer);
		message.setContent(signOff);
		message.setLanguage(meta);
		send(message);
		oppositePlayer = null;
		isPlaying = false;
	}
	
	public void gameOver(){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(oppositePlayer);
		message.setContent(congratz);
		message.setLanguage(meta);
		send(message);
		oppositePlayer = null;
		isPlaying = false;
	}
	
	public void rageMessage(String messageContent){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(oppositePlayer);
		message.setContent(messageContent);
		message.setLanguage(meta);
		send(message);
	}
}
 