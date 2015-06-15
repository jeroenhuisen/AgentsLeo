package opdracht4;

public class LogicalCalculator implements Calculator {
	private int stacks[]={-1,-1,-1};
	
	private int emptyStacks[] = {-1,-1,-1};
	private int amountOfEmptyStacks = 0;
	
	private int matchingStacks[] = {-1,-1,-1};
	private int amountOfMatchingStacks = 0;
	
	private FifteenStack stack;
	
	@Override
	public void calculateStack(FifteenStack stack) {
		this.stack = stack;
		amountOfEmptyStacks = 0;
		amountOfMatchingStacks = 0;
		setValues(stack);
		matchingStacks();
		checkForEmptyStacks();
		/*
		 * if(amountOfEmptyStacks == 1){
		 * 		make stacks equal 
		 *  	EXCEPT if one of the stacks is 1 or 2 etc
		 *  }
		 *  else if(amountOfEmptyStacks == 2){
		 *  	 make not empty stack 1
		 *  	when its already 1 GG.	
		 * }
		 * else if(amountOfEmptyStacks == 0){
		 * 		all 3 stacks avaiable 
		 * 		if(matchingNumbers){
		 * 			put stack which isnt matching to 0
		 * 		}
		 * 		
		 * }else{
		 * 
		 */
		
		if(amountOfEmptyStacks == 1){
			// check if the other 2 stacks are matching
			if(amountOfMatchingStacks >= 1){
				// Almost sertain you will lose
				// Except when the value of the stacks is 1
/**				// Now if statement is useless be aware of this**/
				if(stacks[matchingStacks[0]] > 1){
					//just lower with 1 I guess
					//tryToLower(matchingStacks[0], 1);
					stack.take(matchingStacks[0], 1);
				}else{
					//victory?
					//tryToLower(matchingStacks[0], 1);
					stack.take(matchingStacks[0], 1);
				}
			}else{
				// No matching
				// (Mini)Goal is to make the stacks matching
				// Except when the lowest stack value is 1 
				if(stacks[emptyStacks[0]] == 1){
					// empty the other stack!
					stack.take(matchingStacks[1]+1, matchingStacks[1]);
				}else if(stacks[emptyStacks[1]] == 1){
					// empty the other stack!
					stack.take(matchingStacks[0]+1, matchingStacks[0]);
				}else{
					// Make stacks matching
					makeMatching();
				}
			}
		}
		
	}
	private void setValues(FifteenStack stack){
		for(int i = 0; i < 3; i++){
			stacks[i]= stack.look(i+1); 
		}
	}
	private void checkForEmptyStacks(){
		if(stacks[0] <= 0){
			emptyStacks[amountOfEmptyStacks] = 0;
			amountOfEmptyStacks++;
		}
		if(stacks[1] <= 0){
			emptyStacks[amountOfEmptyStacks] = 1;
			amountOfEmptyStacks++;
		}
		if(stacks[2] <= 0){
			emptyStacks[amountOfEmptyStacks] = 2;
			amountOfEmptyStacks++;
		}
	}
	
	/*private int matchingNumbers(){
		if(stacks[0] == stacks[1]){
			//return stacks[0];
			return 3; //thirdstack might not match 
		}else if(stacks[0] == stacks[2]){
			//return stacks[0];
			return 2; //secondStack might not match
		}else if(stacks[1] == stacks[2]){
			//return stacks[1];
			return 1; //firstStack might not match
		}else{
			return -1; //no matches
		}
	}*/
	
	private void matchingStacks(){
		if(stacks[0] == stacks[1]){
			matchingStacks[amountOfMatchingStacks] = 0;
			matchingStacks[amountOfMatchingStacks++] = 1;
			//not sure if I should higher this.
			amountOfMatchingStacks++;
		}
		if(stacks[0] == stacks[2]){
			matchingStacks[amountOfMatchingStacks] = 0;
			matchingStacks[amountOfMatchingStacks++] = 2;
			//not sure if I should higher this.
			amountOfMatchingStacks++;
		}
		if(stacks[1] == stacks[2]){
			matchingStacks[amountOfMatchingStacks] = 1;
			matchingStacks[amountOfMatchingStacks++] = 2;
			//not sure if I should higher this.
			amountOfMatchingStacks++;
		}
	}
	
	private void makeMatching(){
		//int cmp = stacks[emptyStacks[0]] > stacks[emptyStacks[1]] ? +1 : stacks[emptyStacks[0]] < stacks[emptyStacks[1]] ? -1 : 0;
		if(stacks[emptyStacks[0]] > stacks[emptyStacks[1]]){
			stack.take(emptyStacks[0], stacks[emptyStacks[0]] - stacks[emptyStacks[1]]);
		}else{
			stack.take(emptyStacks[1], stacks[emptyStacks[1]] - stacks[emptyStacks[0]]);
		}
	}

}
