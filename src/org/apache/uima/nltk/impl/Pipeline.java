package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.cas.CAS;
import org.apache.uima.nltk.utils.Cas2ArrayListString;

/**
 *  <b>Pipeline est la classe envoyant l'ensemble des traitements à réaliser au Run Remote Asynchronous Engine, et récupérant le résultats des traitements.</b> 
 *  	<p> Un objet Pipeline est caractérisé par : 
 *  		<ul> 
 *  			<li>Une liste de services à utiliser.</li> 
 *  			<li>Un Run Remote Asynchrnous Engine (classe UIMANLTKcore).</li> 
 *     			<li>Une liste d'instructions à traiter.</li> 
 *  		 </ul>
 *  	</p>
 *  	<p> Pipeline implémente la classe Observer, à l'image de notre API, implémentant le Design Pattern associé
 *  
 *  @see UIMANLTKcore
 *  @see Observer
 *  
 *  @author UIMA-NLTK team 
 *  
 *  @version 1.0 
 *  
 */
public class Pipeline implements Observer{

	/**
	 * La liste des "endPoints" du Pipeline, c'est a dire la liste des services qu'on utilise
	 */
	private ArrayList<String> listeEndPoints;
	
	
	/**
	 * @deprecated
	 * Résultat des traitements des fichiers textuels en entrée (vestigial)
	 */
	private HashMap<String,ArrayList<Collection>> res;
	
	
	/**
	 * Le Run Remote Async Asynchronous Engine personnalisé pour notre API
	 */
	private RRAAEPerso rrae;
	
	
	/**
	 * La liste des instructions lancées par l'utilisateur
	 */
	private ArrayList<String> listInstrs;
	
	
	/**
	 * Constructeur de la classe Pipeline
	 *   	<p> Lis chaque instruction en entrée et :
	 *  		<ul> 
	 *  			<li>Ajoute le endpoint correspondant à la liste des endpoints à utiliser.</li> 
	 *  			<li>Ajoute l'instruction à la liste des instructions à réaliser.</li> 
	 *  		 </ul>
	 *  	</p>
	 *  
	 * @param instructions
	 * 		La liste d'instructions (d'opérations) que l'utilisateur veut réaliser sur le texte
	 * 
	 * @throws Exception
	 */
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
	
	
	/**
	 * Instanciation et lancement du Run Remote Asynchronous Engine
	 * @throws Exception
	 */
	public void run() throws Exception{
		rrae = new RRAAEPerso(listeEndPoints);
		rrae.addObserver(this);
		rrae.run();

	}



	@Override
	/**
	 * Appelé par le Run Remote Asynchronous Engine lorsqu'un fichier à fini d'être traité.
	 * @see Observer
	 * @param arg0, l'objet observé dont vient la notification
	 * @param arg1, le CAS final contenant le traitement
	 */
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
