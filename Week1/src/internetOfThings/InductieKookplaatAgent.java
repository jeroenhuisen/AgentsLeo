package internetOfThings;

import internetOfThings.example.Buyer;

import java.util.Random;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;


public class InductieKookplaatAgent extends Agent{
	public static final String MAIN_HOST = "192.168.184.143";
	public static final String MAIN_PORT = "1099";
	Random rnd = new Random();
	
	private class onderdelen{
		int aanstuurunits = 2;
		int glasplaat = 1;
		int grotePit = 2;
		int kleinePit = 2;
	}
	ArrayList<String> onderdelen = new ArrayList<String>(); 
		
	
	protected void setup() {
		onderdelen.add("aanstuurunit");
		onderdelen.add("aanstuurunit");
		onderdelen.add("glasplaat");
		onderdelen.add("grotePit");
		onderdelen.add("grotePit");
		onderdelen.add("kleinePit");
		onderdelen.add("kleinePit");
		breakdown();
	}
	
	private void breakdown(){
		// Wait till a part breaksdown max 5 seconds
		int waitTime = rnd.nextInt(5000);
		System.out.println(getName() + ": time: " + waitTime);
		try {
			Thread.sleep(waitTime);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Choose which part breaksdown
		int brokenPart = rnd.nextInt(onderdelen.size());
		String brokenPartName = onderdelen.get(brokenPart);
		onderdelen.remove(brokenPart);
		
		// Try to buy a new broken part
		// First pick (random) price between 25 and 100
		int price = rnd.nextInt(100-25) + 25;
		
		//addBuyBehaviour(brokenPartName, price);
	}
	
	// --- generating distinct Random generator -------------------


}
