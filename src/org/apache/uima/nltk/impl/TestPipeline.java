package org.apache.uima.nltk.impl;

public class TestPipeline {

	public static void main(String args[]) throws Exception{
		
		UimaNltk uima = new UimaNltk();
		uima.loadText();
	
		System.out.println(uima.getWordTokens());
	}
}
