package opdracht4;

public class CalculatorHandler implements Calculator{
	private Calculator test;
	
	public CalculatorHandler(String playstyle){
		switch(playstyle){
			case "cheater":{
				// This playstyle will cheat by taking from multiple stacks
				// and never taking the last one just returning them.
				test = new CheaterCalculator();
				break;
			}
			case "random":{
				// No tactics just taking random amount from a random stack
				test = new RandomCalculator();
				break;
			}
			default:{
				System.err.println("No playstyle given using random instead");
				test = new RandomCalculator();
				break;
			}
		}
	}
	
	public void calculateStack(FifteenStack stack){
		test.calculateStack(stack);
	}
}
