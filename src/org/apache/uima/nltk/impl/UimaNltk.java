package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class UimaNltk implements Observer{
	private ArrayList<ArrayList<String>> wordTokens;
	private ArrayList<ArrayList<String>> sentenceTokens;
	private ArrayList<ArrayList<ArrayList<String>>> tagTokens;
	private boolean finished = false;
	
	
	public ArrayList<ArrayList<String>> getWordTokens() {
		while(!getFinished());
		return wordTokens;
	}
	public void setWordTokens(ArrayList<ArrayList<String>> wordTokens) {
		this.wordTokens = wordTokens;
	}
	public ArrayList<ArrayList<String>> getSentenceTokens() {
		while(!getFinished());
		return sentenceTokens;
	}
	public void setSentenceTokens(ArrayList<ArrayList<String>> sentenceTokens) {
		this.sentenceTokens = sentenceTokens;
	}
	public ArrayList<ArrayList<ArrayList<String>>> getTagTokens() {
		while(!getFinished());
		return tagTokens;
	}
	public void setTagTokens(ArrayList<ArrayList<ArrayList<String>>> tagTokens) {
		this.tagTokens = tagTokens;
	}
	
	public boolean getFinished(){
		return this.finished;
	}
	
	public void loadText() throws Exception{
		Pipeline p = new Pipeline();
		p.addObserver(this);
		p.run();
	}
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		HashMap<String, ArrayList<?>> operations = (HashMap<String, ArrayList<?>>) arg1;
		setWordTokens((ArrayList<ArrayList<String>>) operations.get("word"));
		setSentenceTokens((ArrayList<ArrayList<String>>) operations.get("sentence"));
		setTagTokens((ArrayList<ArrayList<ArrayList<String>>>) operations.get("tag"));
		finished = true;
	}
	
	
	

}
