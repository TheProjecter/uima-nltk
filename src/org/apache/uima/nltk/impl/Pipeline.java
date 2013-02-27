package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;

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
		System.out.println("Pipeline : "+sauveCas.isEmpty());
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
	public void update(Observable arg0, Object arg1) {
		if(arg1.toString() == "stop"){
			for(CAS cas: listeCAS){
				System.out.println(cas.getDocumentText());
				AnnotationIndex<AnnotationFS> annotIndex  = cas.getAnnotationIndex();
				FSIterator<AnnotationFS> iterator = annotIndex.iterator();
				while(iterator.hasNext()){
					System.out.println(iterator.get().getType().toString());
				}
			}
		}else{
			CAS cas = (CAS) arg1;
			//On ajoute le CAS à notre liste de CAS
			listeCAS.add(cas);
		}
	}
}
