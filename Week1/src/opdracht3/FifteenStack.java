package opdracht3;

public class FifteenStack {
	private int firstStack = 3;
	private int secondStack = 5;
	private int thirdStack = 7;
	
	public int look(int stack){
		return stack;
	}
	public void take(int stack, int amount){
		
	}
	public boolean gameOver(){
		return true;
	}
	public String toString(){
		return "This is a String";
	}
	public static FifteenStack fromString(String s){
		return new FifteenStack();
	}
}
