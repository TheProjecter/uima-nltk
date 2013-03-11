package org.apache.uima.nltk.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.apache.uima.cas.CAS;
import org.apache.uima.nltk.utils.Cas2ArrayListString;

public class Pipeline extends Observable implements Observer {

	private ArrayList<String> listeEndPoints;
	private HashMap<String, ArrayList<?>> operations;
	private RunRemoteAsyncAE rrae;
	private ArrayList<String> listInstrs;

	public Pipeline(String... instructions) throws Exception {
		listeEndPoints = new ArrayList<String>();
		listInstrs = new ArrayList<String>();
		for (String instr : instructions) {
			if ("lemmatisation".equals(instr)) {
				listeEndPoints.add("EnPOSTaggerQueue");
				listInstrs.add(instr);
			} else if ("decoupage_phrase".equals(instr)) {
				listeEndPoints.add("WhitespaceTokenizerQueue");
				listInstrs.add(instr);
			} else if ("decoupage_mot".equals(instr)) {
				listeEndPoints.add("WhitespaceTokenizerQueue");
				listInstrs.add(instr);
			}

		}
	}

	public Pipeline() throws Exception {
		// Création des listes avec les traitements de chaque texte.
		operations = new HashMap<String, ArrayList<?>>();
		operations.put("word", new ArrayList<ArrayList<String>>());
		operations.put("sentence", new ArrayList<ArrayList<String>>());
		operations.put("tag", new ArrayList<ArrayList<ArrayList<String>>>());

		// Appel des deux endpoints
		listeEndPoints = new ArrayList<String>();
		listeEndPoints.add("WhitespaceTokenizerQueue");
		listeEndPoints.add("EnPOSTaggerQueue");
	}

	public void run() throws Exception {
		rrae = new RunRemoteAsyncAE(listeEndPoints);
		rrae.addObserver(this);
		rrae.run();

	}

	@Override
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
			System.out.println("liste des mots ajouté");

			// Tous les traitements des phrases par chaque texte.

			ArrayList<String> sentenceToken = new ArrayList<String>();
			sentenceToken = Cas2ArrayListString
					.fromCas2ArrayString4Sentence(cas);

			ArrayList<ArrayList<String>> listeSentences = (ArrayList<ArrayList<String>>) operations
					.get("sentence");
			listeSentences.add(sentenceToken);
			operations.put("sentence", listeSentences);
			System.out.println("liste des phrases ajouté");

			// Tous les traitements des tags par chaque texte.

			ArrayList<ArrayList<String>> tagToken = new ArrayList<ArrayList<String>>();
			tagToken = Cas2ArrayListString.fromCas2ArrayString4Postag(cas);

			ArrayList<ArrayList<ArrayList<String>>> listeTag = (ArrayList<ArrayList<ArrayList<String>>>) operations
					.get("tag");
			listeTag.add(tagToken);
			operations.put("word", listeTag);
			System.out.println("liste des tags ajouté");

			System.out
					.println("-----------------------------------------------------------------------------------------------");
//			System.out.println(operations.toString());
			System.out
					.println("-----------------------------------------------------------------------------------------------");
		}
	}
}
