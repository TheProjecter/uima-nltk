package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class UimaNltk implements Observer{
	private ArrayList<ArrayList<String>> wordTokens;
	private ArrayList<ArrayList<String>> sentenceTokens;
	private ArrayList<ArrayList<ArrayList<String>>> tagTokens;
	
	
	public ArrayList<ArrayList<String>> getWordTokens() {
		return wordTokens;
	}
	public void setWordTokens(ArrayList<ArrayList<String>> wordTokens) {
		this.wordTokens = wordTokens;
	}
	public ArrayList<ArrayList<String>> getSentenceTokens() {
		return sentenceTokens;
	}
	public void setSentenceTokens(ArrayList<ArrayList<String>> sentenceTokens) {
		this.sentenceTokens = sentenceTokens;
	}
	public ArrayList<ArrayList<ArrayList<String>>> getTagTokens() {
		return tagTokens;
	}
	public void setTagTokens(ArrayList<ArrayList<ArrayList<String>>> tagTokens) {
		this.tagTokens = tagTokens;
	}
	
	public void loadText() throws Exception{
		Pipeline p = new Pipeline();
		p.run();
	}
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		
		
	}
	
	
	

}
