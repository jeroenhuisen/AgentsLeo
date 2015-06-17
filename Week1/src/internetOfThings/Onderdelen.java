package internetOfThings;

// TODO: even kijken naar of we rekening willen houden met de aantallen van de modules.

public class Onderdelen {
	/*
	 * int aanstuurunits = 2; 
	 * int glasplaat = 1; 
	 * int grotePit = 2; 
	 * int kleinePit = 2; 
	 */
	public static enum Module{
		aanstuurunit,
		glasplaat,
		grotepit,
		kleinepit
	}
	private Module module;
	private boolean isDefect = false;
	
	// Constructor
	public Onderdelen(Module m){	module = m;					}
	
	//Getters & setters
	public boolean 	getDefect(){ 	return isDefect; 			}
	public Module 	getModule(){ 	return module; 				}
	public void		BreakModule(){ 	isDefect = true; 			}
	public String 	toString(){		return module.toString(); 	}
	
}