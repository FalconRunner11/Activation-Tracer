package nv.activation_tracer;

import java.util.ArrayList;

public class StatusMessage {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private ArrayList<String> words;
	private ArrayList<String> types;
	private int nestLevel;
	
	//-----------------------------------------------------------------//
	
	/** Constructors **/
	
	protected StatusMessage(int nL) {
		words = new ArrayList<String>();
		types = new ArrayList<String>();
		nestLevel = nL;
	}
	
	//-----------------------------------------------------------------//
	
	//-----------------------------------------------------------------//
	
	/** Private Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Mutator Methods **/
	
	protected void addWord(String w, String t) {
		words.add(w);
		types.add(t);
	}
	
	//-----------------------------------------------------------------//
	
	/** Accessor Methods **/
	
	protected ArrayList<String> getWords() {
		return words;
	}
	
	protected ArrayList<String> getTypes() {
		return types;
	}
	
	protected int getNestLevel() {
		return nestLevel;
	}
	
	protected String getFullMessageString() {
		String fullString = "";
		for (int i = 0; i < words.size(); i++) {
			fullString += words.get(i);
		}
		return fullString;
	}
	
	//-----------------------------------------------------------------//
	
}
