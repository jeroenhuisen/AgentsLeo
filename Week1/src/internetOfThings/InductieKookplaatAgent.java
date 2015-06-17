package internetOfThings;

import internetOfThings.example.DelayBehaviour;
import internetOfThings.example.myReceiver;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import java.util.ArrayList;

public class InductieKookplaatAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MAIN_HOST = "192.168.184.143";
	public static final String MAIN_PORT = "1099";
	private DFUtils dfutils;
	
	Random rnd = new Random();
	
	int timesTried = 0;
	int bestPrice = 9999;
	
	ACLMessage msg, bestOffer;

	/*
	 * private class onderdelen { 
	 * int aanstuurunits = 2; 
	 * int glasplaat = 1; 
	 * int grotePit = 2; 
	 * int kleinePit = 2; 
	 * }
	 */

	ArrayList<String> onderdelen = new ArrayList<String>();

	protected void setup() {
		this.dfutils       = new DFUtils(this);
		
		onderdelen.add("aanstuurunit");
		onderdelen.add("aanstuurunit");
		onderdelen.add("glasplaat");
		onderdelen.add("grotePit");
		onderdelen.add("grotePit");
		onderdelen.add("kleinePit");
		onderdelen.add("kleinePit");

		// DF register service
		DFAgentDescription template 	= new DFAgentDescription();
		new SearchConstraints();
		ServiceDescription sd 			= new ServiceDescription();
		
		// Set de type en naam van de service.
		sd.setType("BlackMarket");
		sd.setName("BlackMarket");
		
		template.addServices(sd);
		template.setName(getAID());

		try {
			DFService.register(this, template);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		// Subscibe to the DF
		//send(DFService.createSubscriptionMessage(this, getDefaultDF(), template, constraints));
		breakdown();
		//searchDF("BlackMarket");
	}

	private void breakdown() {
		// Wait till a part breaksdown max 5 seconds
		int waitTime = rnd.nextInt(5000);
		System.out.println(getName() + ": time: " + waitTime);
		try {
			Thread.sleep(waitTime);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Choose which part breaksdown
		
		// Why 36 in roulette there are 37 possible outcomes.
		// so this is 36 + the zero.
		// If the nextInt is Zero the house wins and the gambler loses a part of his kit.
		if(rnd.nextInt(36) == 0){
			int brokenPart 			= rnd.nextInt(onderdelen.size());
			onderdelen.get(brokenPart);
			onderdelen.remove(brokenPart);
		}

		// Try to buy a new broken part
		// First pick (random) price between 25 and 100
		//int price = rnd.nextInt(100 - 25) + 25;

		/*
		 * ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		 * message.setOntology("BlackMarket"); message.setContent("test");
		 */

		buyerBehaviour();
	}
	


	
	private void buyerBehaviour(){
		msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.setSender(getAID());
	
		
		MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

		System.out.println("Buyer " + getLocalName() + " gets prices.");
		
		SequentialBehaviour seq = new SequentialBehaviour();
		addBehaviour(seq);

		ParallelBehaviour par = new ParallelBehaviour( ParallelBehaviour.WHEN_ALL );
		seq.addSubBehaviour(par);

		AID[] agents = dfutils.searchDF("BlackMarket");
		System.out.println(agents.length);
		for(int i = 0; i < agents.length; i++){
			msg.addReceiver(agents[i]);
			par.addSubBehaviour(new myReceiver(this, 1000, template) {
				public void handle(ACLMessage msg) {
					if (msg != null) {
						int offer = Integer.parseInt(msg.getContent());
						System.out.println("Got quote $" + offer + " from " + msg.getSender().getLocalName());
						if (offer < bestPrice) {
							bestPrice = offer;
							bestOffer = msg;
						}
					}else{
					}
				}
			});
		}

		seq.addSubBehaviour(new DelayBehaviour(this, rnd.nextInt(200)) {
			public void handleElapsedTimeout() {
				if (bestOffer == null) {
					System.out.println("Got no quotes");
				} else {
					System.out.println("\nBest Price $" + bestPrice + " from "
							+ bestOffer.getSender().getLocalName());
					ACLMessage reply = bestOffer.createReply();
					if (bestPrice <= 80) {
						reply.setPerformative(ACLMessage.REQUEST);
						reply.setContent("" + rnd.nextInt(80));
						System.out.print(" ORDER at " + reply.getContent());
						send(reply);
					}
				}
			}
		});

		seq.addSubBehaviour(new myReceiver(this, 1000, MessageTemplate.or(
						MessageTemplate.MatchPerformative(ACLMessage.AGREE),
						MessageTemplate.MatchPerformative(ACLMessage.REFUSE))) {
			
			public void handle(ACLMessage msg) {
				if (msg != null) {
					System.out.println("Got "
							+ ACLMessage.getPerformative(msg.getPerformative())
							+ " from " + msg.getSender().getLocalName());

					if (msg.getPerformative() == ACLMessage.AGREE){
						System.out.println("  --------- Finished ---------\n");
						auctionSucces();
					}
					else{
						auctionFailed();
					}
				} else {
					System.out.println("==" + getLocalName() + " timed out");
					auctionFailed();
				}
			}
		});

		send(msg);
	}
	
	private void auctionFailed(){

		// wait 5 seconds
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timesTried+=1;
		// Failed try 3 times again.
		if(timesTried >= 3){
			timesTried = 0;
			// Er is nu 3 keer geprobeert om te kijken of er iets te koop is.
			System.out.println("3 fails, now going to sell my shit, uhm stuff...");
			senderBehaviour();

		}else{
			// try again
			buyerBehaviour();
		}
	}
	
	private void auctionSucces(){		timesTried = 0;	}
	
	private void senderBehaviour(){
		
		addBehaviour( new CyclicBehaviour(this) 
		{
			public void action() 
			{
				MessageTemplate query  = MessageTemplate.MatchPerformative( ACLMessage.QUERY_REF );
				ACLMessage msg = receive( query );
				if (msg!=null) 
					addBehaviour( new Transaction(myAgent, msg) );
				block();
			}
		});
	}
	
	
	class Transaction extends SequentialBehaviour 
	{
		ACLMessage msg,
		           reply ;
		String     ConvID ;
		
		int    price  = rnd.nextInt(100);

		public Transaction(Agent a, ACLMessage msg) 
		{
			super( a );
			this.msg 	= msg;
			ConvID 		= msg.getConversationId();
		}
		
		public void onStart() 
		{
			int delay = delay = rnd.nextInt( 200 );
			System.out.println( " - " +
				myAgent.getLocalName() + " <- QUERY from " +
				msg.getSender().getLocalName() + ".  Will answer $" + price + " in " + delay + " ms");
				
			addSubBehaviour( new DelayBehaviour( myAgent, delay)
	      	{
				public void handleElapsedTimeout() { 
					reply = msg.createReply();
					reply.setPerformative( ACLMessage.INFORM );
					reply.setContent("" + price );
					send(reply); 
				}
	      	});

			MessageTemplate template = MessageTemplate.MatchPerformative( ACLMessage.REQUEST );
	    
			addSubBehaviour( new myReceiver( myAgent, 2000, template) 
			{
				public void handle( ACLMessage msg1) 
				{  
					if (msg1 != null ) {
						
						int offer = Integer.parseInt( msg1.getContent());
						System.out.println("Got proposal $" + offer + " from " + msg1.getSender().getLocalName() +
						   " & my price is $" + price );
							
						reply = msg1.createReply();
						if ( offer >= rnd.nextInt(price) )
							reply.setPerformative( ACLMessage.AGREE );
						else
							reply.setPerformative( ACLMessage.REFUSE );
						send(reply);
						System.out.println("  == " + ACLMessage.getPerformative(reply.getPerformative() ));
				   } 
				   else {
				   	System.out.println("Timeout ! quote $" + price + " from " + getLocalName() +
						    " is no longer valid");
					}
				}	
			});
		}
	        
	} 
}
