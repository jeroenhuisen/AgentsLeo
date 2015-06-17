package internetOfThings;

import internetOfThings.Parts.Module;
import internetOfThings.Parts.Part;
import internetOfThings.example.DelayBehaviour;
import internetOfThings.example.Seller;
import internetOfThings.example.myReceiver;

import java.io.IOException;
import java.io.Serializable;
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
	 * private class onderdelen { int aanstuurunits = 2; int glasplaat = 1; int
	 * grotePit = 2; int kleinePit = 2; }
	 */

	// ArrayList<String> onderdelen = new ArrayList<String>();
	Parts parts;

	protected void setup() {
		this.dfutils = new DFUtils(this);
		parts = new Parts();

		/*
		 * onderdelen.add("aanstuurunit"); onderdelen.add("aanstuurunit");
		 * onderdelen.add("glasplaat"); onderdelen.add("grotePit");
		 * onderdelen.add("grotePit"); onderdelen.add("kleinePit");
		 * onderdelen.add("kleinePit");
		 */

		// DF register service
		DFAgentDescription template = new DFAgentDescription();
		new SearchConstraints();
		ServiceDescription sd = new ServiceDescription();

		// Set de type en naam van de service.
		sd.setType("BlackMarket");
		sd.setName("BlackMarket");

		/*
		 * template.addServices(sd); template.setName(getAID());
		 * 
		 * try { DFService.register(this, template); } catch (FIPAException e) {
		 * e.printStackTrace(); }
		 */
		dfutils.register(sd);
		while (!breakdown())
			;
	}

	private boolean breakdown() {
		// Wait till a part breaksdown max 5 seconds
		int waitTime = rnd.nextInt(500);
		// System.out.println(getName() + ": time: " + waitTime);
		try {
			Thread.sleep(waitTime);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Choose which part breaksdown

		// Why 36 in roulette there are 37 possible outcomes.
		// so this is 36 + the zero.
		// If the nextInt is Zero the house wins and the gambler loses a part of
		// his kit.
		if (rnd.nextInt(36) == 0) {
			/*
			 * int brokenPart = rnd.nextInt(onderdelen.size());
			 * onderdelen.get(brokenPart); onderdelen.remove(brokenPart);
			 */
			if (parts.amountOfParts() > 1) {
				int brokenPartIndex = rnd.nextInt(parts.amountOfParts());
				Part brokenPart = parts.getPart(brokenPartIndex);
				parts.breakPart(brokenPart);

				// Try to buy a new part
				buyerBehaviour(brokenPart);
				return true;
			}
		}
		return false;

	}

	private void buyerBehaviour(Part brokenPart) {
		msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.setSender(getAID());
		try {
			// I want a new part this 1 is broken
			msg.setContentObject(brokenPart);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// msg.setContent("Part:brokenPart.getModule().toString());

		MessageTemplate template = MessageTemplate
				.MatchPerformative(ACLMessage.INFORM);

		System.out.println("Buyer " + getLocalName() + " gets prices.");

		SequentialBehaviour seq = new SequentialBehaviour();
		addBehaviour(seq);

		ParallelBehaviour par = new ParallelBehaviour(
				ParallelBehaviour.WHEN_ALL);
		seq.addSubBehaviour(par);

		AID[] agents = dfutils.searchDF("BlackMarket");
		System.out.println(agents.length);
		for (int i = 0; i < agents.length; i++) {
			msg.addReceiver(agents[i]);
			par.addSubBehaviour(new myReceiver(this, 1000, template) {
				public void handle(ACLMessage msg) {
					if (msg != null) {
						System.out.println("msg received");

						try {
							System.out.println("Got part"
									+ msg.getContentObject());
							Part receivedPart = (Part) msg.getContentObject();
							int offer = receivedPart.price;
							System.out.println("Got quote $" + offer + " from "
									+ msg.getSender().getLocalName());
							if (offer < bestPrice) {
								bestPrice = offer;
								bestOffer = msg;
							}
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						System.err.println("msg == null");
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
						// reply.setContent("" + rnd.nextInt(80));
						try {
							reply.setContentObject(bestOffer.getContentObject());
						} catch (IOException | UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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

					if (msg.getPerformative() == ACLMessage.AGREE) {
						System.out.println("  --------- Finished ---------\n");
						try {
							System.out.println(parts.toString());
							parts.addPart((Part) msg.getContentObject());
							System.out.println(parts.toString());
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						auctionSucces();
					} else {
						try {
							auctionFailed((Part) msg.getContentObject());
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					System.out.println("==" + getLocalName() + " timed out");
					// TODO HELP
					// auctionFailed((Part)msg.getContentObject());
					// failed done.
					senderBehaviour();
				}
			}
		});
		send(msg);
	}

	private void auctionFailed(Part brokenPart) {

		// wait 1 second
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timesTried += 1;
		// Failed try 3 times again.
		if (timesTried >= 3) {
			timesTried = 0;
			// Er is nu 3 keer geprobeert om te kijken of er iets te koop is.
			System.out.println("3 fails, now going to sell my parts now");
			senderBehaviour();

		} else {
			// try again
			buyerBehaviour(brokenPart);
		}
	}

	private void auctionSucces() {
		timesTried = 0;
	}

	private void senderBehaviour() {
		System.out.println("StartSenderBehaviour");
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				MessageTemplate query = MessageTemplate
						.MatchPerformative(ACLMessage.QUERY_REF);
				ACLMessage msg = receive(query);
				if (msg != null) {
					try {
						Part requestedPart = (Part) msg.getContentObject();
						System.out.println("recievedMessage");
						System.out
								.println(requestedPart.getModule().toString());
						if (parts.availablePart(requestedPart)) {
							System.out.println("try to sell part");
							addBehaviour(new Transaction(myAgent, msg));
						}
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				block();
			}
		});
	}

	class Transaction extends SequentialBehaviour {
		ACLMessage msg, reply;
		String ConvID;

		int price = rnd.nextInt(100);

		public Transaction(Agent a, ACLMessage msg) {
			super(a);
			this.msg = msg;
			ConvID = msg.getConversationId();
		}

		public void onStart() {
			int delay = delay = rnd.nextInt(200);
			System.out.println(" - " + myAgent.getLocalName()
					+ " <- QUERY from " + msg.getSender().getLocalName()
					+ ".  Will answer $" + price + " in " + delay + " ms");

			addSubBehaviour(new DelayBehaviour(myAgent, delay) {
				public void handleElapsedTimeout() {
					reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					try {
						System.out.print("REPLY" + msg.getContentObject());
						Part part = (Part) msg.getContentObject();
						part.price = price;
						reply.setContentObject(part);
					} catch (IOException | UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// reply.setContent("" + price );
					send(reply);
				}
			});

			MessageTemplate template = MessageTemplate
					.MatchPerformative(ACLMessage.REQUEST);

			addSubBehaviour(new myReceiver(myAgent, 2000, template) {
				public void handle(ACLMessage msg1) {
					if (msg1 != null) {
						try {
							Part part = (Part) msg1.getContentObject();

							int offer = part.price;

							System.out.println("Got proposal $" + offer
									+ " from "
									+ msg1.getSender().getLocalName()
									+ " & my price is $" + price);

							reply = msg1.createReply();
							if (offer >= rnd.nextInt(price)) {
								reply.setPerformative(ACLMessage.AGREE);

								Part newPart = parts.getPart(parts
										.findPart(part));
								parts.removePart(part);
								reply.setContentObject(newPart);

							}

							else
								reply.setPerformative(ACLMessage.REFUSE);
							try {
								reply.setContentObject(msg.getContentObject());
							} catch (IOException | UnreadableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							send(reply);
							System.out.println("  == "
									+ ACLMessage.getPerformative(reply
											.getPerformative()));
						} catch (IOException | UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.out.println("Timeout ! quote $" + price
								+ " from " + getLocalName()
								+ " is no longer valid");
					}
				}
			});
		}

	}
}
