package opdracht4;

import java.io.Serializable;

public class FifteenStack implements Serializable{
	private static final boolean bullshitrules = true; 
	private int firstStack = 3;
	private int secondStack = 5;
	private int thirdStack = 7;
	
	public int look(int stack){
		switch(stack){
			case 1:{
				return firstStack;
			}
			case 2:{
				return secondStack;
			}
			case 3:{
				return thirdStack;
			}
			default:{
				System.err.println("wrong stack value: look " + stack);
				return -1;
			}
		}
	}
	public void take(int stack, int amount){
		if(amount > 0){
			switch(stack){
				case 1:{
					firstStack -= amount;
					break;
				}
				case 2:{
					secondStack -= amount;
					break;
				}
				case 3:{
					thirdStack -= amount;
					break;
				}
				default:{
					System.err.println("wrong stack value: take " + stack);
					break;
				}
			}
		}else{
			System.err.println("You are a cheater!");
			System.err.println("The amount you take should be bigger than 0!");
		}
	}
	public boolean gameOver(){
		if(bullshitrules){
			if(firstStack <= 0 && secondStack <= 0 && thirdStack <= 0){
				return true;
			}
			return false;
		}else{
			if(firstStack <= 0){
				return true;
			}else if(secondStack <= 0){
				return true;
			}else if(thirdStack <= 0){
				return true;
			}
			return false;
		}
	}
	public String toString(){
		return "Stack 1: " + firstStack +"\n" +
				"Stack 2: " + secondStack + "\n" +
				"Stack 3: " + thirdStack + "\n";
				
	}
	public static FifteenStack fromString(String s){
		return new FifteenStack();
	}
}
