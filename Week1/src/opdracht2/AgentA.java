package opdracht2;

import jade.core.behaviours.CyclicBehaviour;

public class AgentA extends jade.core.Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2161863190929663356L;

	public void setup(){
		addBehaviour(new CyclicBehaviour(){
			private static final long serialVersionUID = 1L;

			public void action(){ 
				//System.out.println("I'm still alive!"); 
			}
		});
		System.out.println("Agent: " + getLocalName() + " started");
	}
}
