package opdracht3;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentA extends jade.core.Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2161863190929663356L;

	protected void setup(){
		addBehaviour(new CyclicBehaviour(){
			private static final long serialVersionUID = 1L;

			public void action(){ 
				ACLMessage msg = receive();
				if (msg != null){ 
					System.out.println(msg.getContent());	
					ACLMessage message = new ACLMessage(ACLMessage.INFORM);
					message.addReceiver(msg.getSender());
					message.setContent("Hello!");
					send(message);
				}
				block(); 
			}
		});
		System.out.println("Agent: " + getLocalName() + " started");
	}
}
