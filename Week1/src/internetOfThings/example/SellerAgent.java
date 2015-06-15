package internetOfThings.example;

// http://www.iro.umontreal.ca/~vaucher/Agents/Jade/primer6.html

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SellerAgent extends Agent 
{
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup(){
	      addBehaviour( new CyclicBehaviour(this) 
	      {
	         /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void action() 
	         {
	            ACLMessage msg = receive( MessageTemplate.MatchPerformative
	                                          ( ACLMessage.QUERY_REF ) );
	            if (msg!=null) 
	               addBehaviour( new Transaction(myAgent, msg) );
	            block();
	         }
	      });
	   }
	
	
}