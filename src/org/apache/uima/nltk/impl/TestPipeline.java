package org.apache.uima.nltk.impl;

public class TestPipeline {

	public static void main(String args[]) throws Exception{

		Pipeline p = new Pipeline("decoupage_phrase","lemmatisation");
		p.run();
	}
}
