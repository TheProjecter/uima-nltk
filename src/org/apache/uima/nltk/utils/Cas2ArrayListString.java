package org.apache.uima.nltk.utils;

import java.util.ArrayList;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;


/**
 *  <b>Cas2ArrayListString est une classe "outil" permettant la transformation d'un objet CAS contenant des annotations en un objet de type ArrayList.</b> 
 *  	<p> Cette classe permet les transformatins suivante : 
 *  		<ul> 
 *  			<li>CAS -> ArrayList, avec les annotations correspondant au d�coupage en mots.</li> 
 *  			<li>CAS -> ArrayList, avec les annotations correspondant au d�coupage en phrases.</li> 
 *     			<li>CAS -> ArrayList, avec les annotations correspondant � l'�tiquetage grammatical.</li> 
 *  		 </ul>
 *  	</p>
 *  
 *  @see CAS
 *  @see TokenAnnotation
 *  @see SentenceAnnotation
 *  
 *  @author UIMA-NLTK team 
 *  
 *  @version 1.0 
 *  
 */
public class Cas2ArrayListString {

	
	/**
	 * Traite un CAS et retourne une liste de mots
	 * @param aCas, le CAS � traiter
	 * @return liste de mots
	 */
	public static ArrayList<String> fromCas2ArrayString4Tokenization(CAS aCas)
	{
		//la liste qui sera retournée
		ArrayList<String> array = new ArrayList<String>();
				
		//le texte contenu dans le CAS
		String text = aCas.getDocumentText();
		
		//les annotations du texte
		AnnotationIndex<AnnotationFS> annotIndex = aCas.getAnnotationIndex();
		FSIterator<AnnotationFS> iter = annotIndex.iterator();
		
		
		//ajout dans la liste du texte correspondant à chaque annotation "TokenAnnotation" => ajout de tous les mots du texte dans la liste
		while(iter.hasNext())
		{
			if(iter.get().getType().toString().equals("org.apache.uima.TokenAnnotation"))
			{
				array.add(text.substring(iter.get().getBegin(), iter.get().getEnd()));
			}
			
			iter.moveToNext();
		}
		
			
		return array;
	}
	
	
	
	
	
	
	
	/**
	 * Traite un CAS et retourne une liste de phrase
	 * @param aCas, le CAS à traiter
	 * @return liste de phrases
	 */
	public static ArrayList<String> fromCas2ArrayString4Sentence(CAS aCas)
	{
		//la liste qui sera retournée
		ArrayList<String> array = new ArrayList<String>();
				
		//le texte contenu dans le CAS
		String text = aCas.getDocumentText();
		
		//les annotations du texte
		AnnotationIndex<AnnotationFS> annotIndex = aCas.getAnnotationIndex();
		FSIterator<AnnotationFS> iter = annotIndex.iterator();
		
		
		//ajout dans la liste du texte correspondant à chaque annotation "SentenceAnnotation" => ajout de toutes les phrases du texte dans la liste
		while(iter.hasNext())
		{
			if(iter.get().getType().toString().equals("org.apache.uima.SentenceAnnotation"))
			{
				array.add(text.substring(iter.get().getBegin(), iter.get().getEnd()));
			}
			
			iter.moveToNext();
		}
		
			
		return array;
	}
	
	
	
	
	
	
	/**
	 * Traite un CAS et retourne une liste de liste de mots et nature grammaticale
	 * @param aCas, le CAS à traiter
	 * @return liste de liste de mots et nature grammaticale
	 */
	public static ArrayList<ArrayList<String>> fromCas2ArrayString4Postag(CAS aCas)
	{
		//la liste qui sera retournée
		ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
				
		//le texte contenu dans le CAS
		String text = aCas.getDocumentText();
		
		//les annotations du texte
		AnnotationIndex<AnnotationFS> annotIndex = aCas.getAnnotationIndex();
		FSIterator<AnnotationFS> iter = annotIndex.iterator();
		
		
		//ajout dans la liste du texte correspondant à chaque annotation "TokenAnnotation", ainsi que la nature grammaticale de chaque mot
		while(iter.hasNext())
		{
			if(iter.get().getType().toString().equals("org.apache.uima.TokenAnnotation"))
			{
				ArrayList<String> temp = new ArrayList<String>();
				
				//rep�rage du type de "sous annotation" qui correspond à la nature grammaticale
				Type type = iter.get().getType();
				Feature feat = type.getFeatureByBaseName("posTag");
				
				//le texte ( = le mot)
				temp.add(text.substring(iter.get().getBegin(), iter.get().getEnd()));
				//la nature grammaticale
				temp.add(iter.get().getStringValue(feat));
								
				array.add(temp);
			}
			
			iter.moveToNext();
		}
		
			
		return array;
	}
	
	
	
}
