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
public class Pipeline extends Observable implements Observer {

	/**
	 * La liste des "endPoints" du Pipeline, c'est a dire la liste des services qu'on utilise
	 */
	private ArrayList<String> listeEndPoints;
	
	/**
	 * Liste des opérations par texte
	 */
	private HashMap<String, ArrayList<?>> operations;
	
	/**
	 * Le Run Remote Async Asynchronous Engine personnalisé pour notre API
	 */
	private RunRemoteAsyncAE rrae;
	
	/**
	 * La liste des instructions lancées par l'utilisateur
	 */
	private ArrayList<String> listInstrs;

	/**
	 * Constructeur de la classe Pipeline
	 * 
	 * @throws Exception
	 */

	public Pipeline() throws Exception {
		// CrÃ©ation des listes avec les traitements de chaque texte.
		operations = new HashMap<String, ArrayList<?>>();
		operations.put("word", new ArrayList<ArrayList<String>>());
		operations.put("sentence", new ArrayList<ArrayList<String>>());
		operations.put("tag", new ArrayList<ArrayList<ArrayList<String>>>());

		// Appel des deux endpoints
		listeEndPoints = new ArrayList<String>();
		listeEndPoints.add("WhitespaceTokenizerQueue");
		listeEndPoints.add("EnPOSTaggerQueue");
	}
	

	/**
	 * Instanciation et lancement du Run Remote Asynchronous Engine
	 * @throws Exception
	 */
	public void run() throws Exception {
		rrae = new RunRemoteAsyncAE(listeEndPoints);
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
	public void update(Observable arg0, Object arg1) {
		if (arg1 == null) {
			setChanged();
			notifyObservers(operations);
		} else {
			CAS cas = (CAS) arg1;

			// Tous les traitements des mots par chaque texte.

			ArrayList<String> wordToken = new ArrayList<String>();
			wordToken = Cas2ArrayListString
					.fromCas2ArrayString4Tokenization(cas);

			ArrayList<ArrayList<String>> listeWords = (ArrayList<ArrayList<String>>) operations
					.get("word");
			listeWords.add(wordToken);
			operations.put("word", listeWords);

			// Tous les traitements des phrases par chaque texte.

			ArrayList<String> sentenceToken = new ArrayList<String>();
			sentenceToken = Cas2ArrayListString
					.fromCas2ArrayString4Sentence(cas);

			ArrayList<ArrayList<String>> listeSentences = (ArrayList<ArrayList<String>>) operations
					.get("sentence");
			listeSentences.add(sentenceToken);
			operations.put("sentence", listeSentences);

			// Tous les traitements des tags par chaque texte.

			ArrayList<ArrayList<String>> tagToken = new ArrayList<ArrayList<String>>();
			tagToken = Cas2ArrayListString.fromCas2ArrayString4Postag(cas);

			ArrayList<ArrayList<ArrayList<String>>> listeTag = (ArrayList<ArrayList<ArrayList<String>>>) operations
					.get("tag");
			listeTag.add(tagToken);
			operations.put("tag", listeTag);

		}
	}
}
