package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.cas.CAS;
import org.apache.uima.nltk.utils.Cas2ArrayListString;


public class Pipeline implements Observer{

	private ArrayList<String> listeEndPoints;
	private HashMap<String,ArrayList<Collection>> res;
	private RRAAEPerso rrae;
	private ArrayList<String> listInstrs;
	
	
	public Pipeline(String... instructions) throws Exception{
		listeEndPoints = new ArrayList<String>();
		listInstrs = new ArrayList<String>();
		for(String instr: instructions){
			if("lemmatisation".equals(instr)){
				listeEndPoints.add("EnPOSTaggerQueue");
				listInstrs.add(instr);
			}else if("decoupage_phrase".equals(instr)){
				listeEndPoints.add("WhitespaceTokenizerQueue");
				listInstrs.add(instr);
			}
			else if("decoupage_mot".equals(instr)){
				listeEndPoints.add("WhitespaceTokenizerQueue");
				listInstrs.add(instr);
			}
			
		}
	}
	
	public void run() throws Exception{
		rrae = new RRAAEPerso(listeEndPoints);
		rrae.addObserver(this);
		rrae.run();

	}



	@Override
	public void update(Observable arg0, Object arg1){
		CAS cas = (CAS) arg1;
		if(listInstrs.contains("decoupage_mot")){
			ArrayList<String> res = new ArrayList<String>();
			res=Cas2ArrayListString.fromCas2ArrayString4Tokenization(cas);
			System.out.println(res);
		}
		if(listInstrs.contains("decoupage_phrase"))
		{
			ArrayList<String> res = new ArrayList<String>();
			res=Cas2ArrayListString.fromCas2ArrayString4Sentence(cas);
			System.out.println(res);
		}
		if(listInstrs.contains("lemmatisation"))
		{
			ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
			res=Cas2ArrayListString.fromCas2ArrayString4Postag(cas);
			System.out.println(res);
		}
	}
}
