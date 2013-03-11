package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.cas.CAS;
import org.apache.uima.nltk.utils.Cas2ArrayListString;

/**
 *  <b>Pipeline est la classe envoyant l'ensemble des traitements � r�aliser au Run Remote Asynchronous Engine, et r�cup�rant le r�sultats des traitements.</b> 
 *  	<p> Un objet Pipeline est caract�ris� par : 
 *  		<ul> 
 *  			<li>Une liste de services � utiliser.</li> 
 *  			<li>Un Run Remote Asynchrnous Engine (classe UIMANLTKcore).</li> 
 *     			<li>Une liste d'instructions � traiter.</li> 
 *  		 </ul>
 *  	</p>
 *  	<p> Pipeline impl�mente la classe Observer, � l'image de notre API, impl�mentant le Design Pattern associ�
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
	 * R�sultat des traitements des fichiers textuels en entr�e (vestigial)
	 */
	private HashMap<String,ArrayList<Collection>> res;
	
	
	/**
	 * Le Run Remote Async Asynchronous Engine personnalis� pour notre API
	 */
	private RunRemoteAsyncAE rrae;
	
	
	/**
	 * La liste des instructions lanc�es par l'utilisateur
	 */
	private ArrayList<String> listInstrs;
	
	
	/**
	 * Constructeur de la classe Pipeline
	 *   	<p> Lis chaque instruction en entr�e et :
	 *  		<ul> 
	 *  			<li>Ajoute le endpoint correspondant � la liste des endpoints � utiliser.</li> 
	 *  			<li>Ajoute l'instruction � la liste des instructions � r�aliser.</li> 
	 *  		 </ul>
	 *  	</p>
	 *  
	 * @param instructions
	 * 		La liste d'instructions (d'op�rations) que l'utilisateur veut r�aliser sur le texte
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
		rrae = new RunRemoteAsyncAE(listeEndPoints);
		rrae.addObserver(this);
		rrae.run();

	}



	@Override
	/**
	 * Appel� par le Run Remote Asynchronous Engine lorsqu'un fichier � fini d'�tre trait�.
	 * @see Observer
	 * @param arg0, l'objet observ� dont vient la notification
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
