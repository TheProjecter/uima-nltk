package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.cas.CAS;

public class Pipeline implements Observer{

	private ArrayList<String> listeInstr;
	private ArrayList<CAS> listeCAS;
	private RRAAEPerso rrae;
	
	public Pipeline(String... instructions) throws Exception{
		listeCAS = new ArrayList<CAS>();
		listeInstr = new ArrayList<String>();
		for(String instr: instructions){
			listeInstr.add(instr);
		}
	}
	
	public void run() throws Exception{
		for(String instr: listeInstr){
			ArrayList<CAS> sauveCas = new ArrayList<CAS>();
			sauveCas.addAll(listeCAS);
			listeCAS.clear();
			if("lemmatisation".equals(instr)){
				this.lemmatisation(sauveCas);

			}else if("decoupage_phrase".equals(instr)){
				this.decoupagePhrase(sauveCas);
			}
		}

	}
	
	private void decoupagePhrase(ArrayList<CAS> sauveCas) throws Exception {
		/* On met les param par défaut de ce traitement */
		rrae = new RRAAEPerso("WhitespaceTokenizerQueue", sauveCas);
		rrae.addObserver(this);
		rrae.run();
	}

	public void lemmatisation(ArrayList<CAS> sauveCas) throws Exception{
		/* On met les param par défaut de ce traitement */
		rrae = new RRAAEPerso("EnPOSTaggerQueue",sauveCas);
		rrae.addObserver(this);
		rrae.run();
	}

	@Override
	public void update(Observable arg0, Object arg1){
	/*	HashMap<String, CAS> map = (HashMap<String, CAS>) arg1;
		
		CasCopier.copyCas(map.get("casPlein"),map.get("casVide"), true);
		listeCAS.add(map.get("casVide"));
		System.out.println("CAS vide ?");
		System.out.println(listeCAS.get(0).getDocumentText() == null);
	*/
		CAS cas =(CAS) arg1;
		listeCAS.add(cas);
	}
}
