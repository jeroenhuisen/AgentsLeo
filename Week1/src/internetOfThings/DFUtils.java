package internetOfThings;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFUtils {
private Agent agent;
	
	public DFUtils(Agent agent){
		this.agent = agent;
	}

	public void register(ServiceDescription sd)
    {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        dfd.addServices(sd);

        try { DFService.register(agent, dfd ); }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }
	
	public void deregister(){
		try{ DFService.deregister(agent);
		}catch(FIPAException fe) { fe.printStackTrace(); }
	}
	
	AID[] searchDF(String service) {
		System.out.println("searchDF");
		DFAgentDescription dfd 	= new DFAgentDescription();
		ServiceDescription sd 	= new ServiceDescription();
		sd.setType(service);
		dfd.addServices(sd);

		SearchConstraints ALL 	= new SearchConstraints();
		ALL.setMaxResults(new Long(-1));

		try {
			DFAgentDescription[] result = DFService.search(agent, dfd, ALL);
			AID[] agents 		= new AID[result.length-1];
			int iA = 0;
			for (int i = 0; i < result.length; i++){
				// dont add yourself, no need to send a message to yourself :D
				if(!result[i].getName().equals(agent.getAID())){
					agents[iA] = result[i].getName();
					iA++;
				}
			}
			System.out.println(agents.length);
			return agents;

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		return null;
	}
}
