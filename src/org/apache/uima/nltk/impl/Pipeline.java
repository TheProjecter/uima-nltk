package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.cas.CAS;
import org.apache.uima.nltk.utils.Cas2ArrayListString;


public class Pipeline implements Observer{

	private ArrayList<String> listeInstr;
	private RRAAEPerso rrae;
	
	
	public Pipeline(String... instructions) throws Exception{
		listeInstr = new ArrayList<String>();
		for(String instr: instructions){
			if("lemmatisation".equals(instr)){
				listeInstr.add("EnPOSTaggerQueue");
			}else if("decoupage_phrase".equals(instr)){
				listeInstr.add("WhitespaceTokenizerQueue");
			}
			
		}
	}
	
	public void run() throws Exception{
		rrae = new RRAAEPerso(listeInstr);
		rrae.run();

	}


	@Override
	public void update(Observable arg0, Object arg1){

	}
}
