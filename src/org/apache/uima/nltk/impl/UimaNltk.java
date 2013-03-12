package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;



/**
 *  <b>Cas2ArrayListString est une classe "outil" permettant la transformation d'un objet CAS contenant des annotations en un objet de type ArrayList.</b> 
 *
 *  
 *  @author UIMA-NLTK team 
 *  
 *  @version 1.0 
 *  
 */
public class UimaNltk implements Observer{
	
	/**
	 * Liste des textes et des mots de chaque texte
	 */
	private ArrayList<ArrayList<String>> wordTokens;
	
	/**
	 * Liste des textes et des phrases de chaque texte
	 */
	private ArrayList<ArrayList<String>> sentenceTokens;
	
	/**
	 * Liste des textes et des étiquetages grammatiquaux de chaque texte
	 */
	private ArrayList<ArrayList<ArrayList<String>>> tagTokens;
	
	/**
	 * Indique si un texte a fini d'etre traité
	 */
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
	
	
	/**
	 * Crée un objet Pipeline et lance le traitement des textes
	 * @throws Exception
	 */
	public void loadText() throws Exception{
		Pipeline p = new Pipeline();
		p.addObserver(this);
		p.run();
	}
	
	
	@Override
	/**	Des qu'un traitement est fini, on met le résultat dans les variables attendues
	 * @params arg0
	 * @params arg1
	 */
	public void update(Observable arg0, Object arg1) {
		HashMap<String, ArrayList<?>> operations = (HashMap<String, ArrayList<?>>) arg1;
		setWordTokens((ArrayList<ArrayList<String>>) operations.get("word"));
		setSentenceTokens((ArrayList<ArrayList<String>>) operations.get("sentence"));
		setTagTokens((ArrayList<ArrayList<ArrayList<String>>>) operations.get("tag"));
		finished = true;
	}
	
	
	

}
