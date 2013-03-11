package org.apache.uima.nltk.impl;

public class TestPipeline {

	public static void main(String args[]) throws Exception{
	/*	Pipeline p = new Pipeline();
		p.run();
		*/
		
		UimaNltk uima2 = new UimaNltk();
		uima2.loadText();
		while(!uima2.getFinished()){
			
		}
		System.out.println(uima2.getSentenceTokens().toString());
	}
}
