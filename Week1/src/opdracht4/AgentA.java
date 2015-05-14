package opdracht4;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;


public class AgentA extends jade.core.Agent{

	private static final long serialVersionUID = 2161863190929663356L;
	

	
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
		message.setContent(Constants.introduce);
		message.setLanguage(Constants.meta);
		
		ACLMessage messageStart = new ACLMessage(ACLMessage.PROPOSE);
		messageStart.setContent(Constants.startGame);
		messageStart.setLanguage(Constants.meta);
		
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
		addBehaviour(new MessageHandlerBehaviour(this));
		
		
	}
	
	public void startGame(){
		FifteenStack fs = new FifteenStack();
		isPlaying = true;
		makeMove(fs);
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
		message.setLanguage(Constants.game);	
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
		message.setContent(Constants.signOff);
		message.setLanguage(Constants.meta);
		send(message);
		oppositePlayer = null;
		isPlaying = false;
	}
	
	public void gameOver(){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(oppositePlayer);
		message.setContent(Constants.congratz);
		message.setLanguage(Constants.meta);
		send(message);
		oppositePlayer = null;
		isPlaying = false;
	}
	
	public void rageMessage(String messageContent){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(oppositePlayer);
		message.setContent(messageContent);
		message.setLanguage(Constants.meta);
		send(message);
	}
	
	public boolean isPlaying(){
		return isPlaying;
	}
	
	public AID getOppositePlayer(){
		return oppositePlayer;
	}
	
	public void setOppositePlayer(AID player){
		oppositePlayer = player;
	}
}
 