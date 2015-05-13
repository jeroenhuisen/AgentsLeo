package opdracht4;

import java.util.Random;

public class RandomCalculator implements Calculator {
	private Random rand = null;
	@Override
	public void calculateStack(FifteenStack stack) {
		// calculate stuff.
		int stackValue = chooseStack(stack);
		int amount = chooseAmount(stack, stackValue);
		
		stack.take(stackValue, amount);
	}
	
	private int chooseStack(FifteenStack stack){
		
		int stackValue = randInt(1, 3);
		while(stack.look(stackValue) <= 0){
			stackValue = randInt(1, 3);
		}
		return stackValue;
	}
	
	private int chooseAmount(FifteenStack stack, int stackValue){
		return randInt(1, stack.look(stackValue));
	}
	
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 * 
	 * http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
	 * Greg Case
	 */
	public int randInt(int min, int max) {
		if(rand == null){
		    rand = new Random();
		}

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
