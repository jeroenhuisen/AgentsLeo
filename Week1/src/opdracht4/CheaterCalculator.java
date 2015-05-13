package opdracht4;

public class CheaterCalculator implements Calculator{

	@Override
	public void calculateStack(FifteenStack stack) {
		//calculate stuff.
		int value = stack.look(1);
		if(value >= 1){
			stack.take(1, value-1);
		}
		value = stack.look(2);
		stack.take(2, value);
		value = stack.look(3);
		stack.take(3, value);
		
		if(stack.gameOver()){
			System.err.println("I am bad");
		}
	}

}
