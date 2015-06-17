package internetOfThings;


import java.io.Serializable;
import java.util.ArrayList;

import opdracht4.FifteenStack;

public class Parts implements Serializable{
	
	public static enum Module{
		aanstuurunit,
		glasplaat,
		grotepit,
		kleinepit
	}

	public class Part implements Serializable{
		private Module module;
		private boolean isDefect = false;
		public int price;
		
		// Constructor
		public Part(){}
		public Part(Module m){			module = m;					}
		
		//Getters & setters
		public boolean 	getDefect(){ 	return isDefect; 			}
		public Module 	getModule(){ 	return module; 				}
		public void		BreakModule(){ 	isDefect = true; 			}
		public String 	toString(){		return module.toString(); 	}
		
	}
	// Can be sold
	ArrayList<Part> partsList = new ArrayList<Part>();
	// Can be bought
	ArrayList<Part> brokenPartsList = new ArrayList<Part>();
	
	Parts(){
		addPart(new Part(Module.aanstuurunit));
		addPart(new Part(Module.aanstuurunit));
		addPart(new Part(Module.glasplaat));
		addPart(new Part(Module.grotepit));
		addPart(new Part(Module.grotepit));
		addPart(new Part(Module.kleinepit));
		addPart(new Part(Module.kleinepit));
	}
	
	public boolean addPart(Part p){
		if(!partsList.contains(p)){
			partsList.add(p);
			return true;
		}
		return false;
	}
	
	public boolean removePart(Part p){
		// Doesn't work? I think? 
		return partsList.remove(p);
	}
	
	public boolean breakPart(Part p){
		if(partsList.remove(p)){
			p.BreakModule();
			brokenPartsList.add(p);
			return true;
		}
		return false;
	}
	
	public int amountOfParts(){
		return partsList.size();
	}
	
	public int amountOfBrokenParts(){
		return brokenPartsList.size();
	}
	
	public Part getPart(int index){
		return partsList.get(index);
	}
	
	public boolean availablePart(Part p){
		for(int i = 0; i < partsList.size(); i++){
			if(partsList.get(i).getModule().equals(p.getModule())){
				return true;
			}
			
		}
		return false;
	}
	
	public int findPart(Part p){
		for(int i = 0; i < partsList.size(); i++){
			if(partsList.get(i).getModule().equals(p.getModule())){
				return i;
			}
			
		}
		return -1;
	}
	public String toString(){
		return partsList.toString();
	}
}
