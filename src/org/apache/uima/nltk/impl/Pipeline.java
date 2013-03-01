package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.cas.CAS;
import org.apache.uima.nltk.utils.Cas2ArrayListString;


public class Pipeline implements Observer{

	private ArrayList<String> listeInstr;
	private HashMap<String,ArrayList<Collection>> res;
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
		rrae.addObserver(this);
		rrae.run();

	}

	public ArrayList<String> getLemmatisation(){
		return null;
	}

	@Override
	public void update(Observable arg0, Object arg1){
		CAS cas = (CAS) arg1;
		if(listeInstr.contains("decoupage_mot")){
			
		}
		/*ArrayList<String> res = Cas2ArrayListString.fromCas2ArrayString4Tokenization(cas);

		for(String s: res){
			System.out.println(s);
		}*/
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		res=Cas2ArrayListString.fromCas2ArrayString4Postag(cas);
		System.out.println(res);
	}
}
