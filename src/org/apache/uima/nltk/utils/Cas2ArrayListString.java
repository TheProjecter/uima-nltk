package org.apache.uima.nltk.utils;

import java.util.ArrayList;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;


public class Cas2ArrayListString {

	public static ArrayList<String> fromCas2ArrayString4Tokenization(CAS aCas)
	{
		ArrayList<String> array = new ArrayList<String>();
				
		String text = aCas.getDocumentText();
		
		AnnotationIndex<AnnotationFS> annotIndex = aCas.getAnnotationIndex();
		FSIterator<AnnotationFS> truc = annotIndex.iterator();
		
		
		
		while(truc.hasNext())
		{
			if(truc.get().getType().toString().equals("org.apache.uima.TokenAnnotation"))
			{
				array.add(text.substring(truc.get().getBegin(), truc.get().getEnd()));
			}
			
			truc.moveToNext();
		}
		
			
		return array;
	}
	
	
	
	
	
	
	
	
	public static ArrayList<String> fromCas2ArrayString4Sentence(CAS aCas)
	{
		ArrayList<String> array = new ArrayList<String>();
				
		String text = aCas.getDocumentText();
		
		AnnotationIndex<AnnotationFS> annotIndex = aCas.getAnnotationIndex();
		FSIterator<AnnotationFS> truc = annotIndex.iterator();
		
		
		
		while(truc.hasNext())
		{
			if(truc.get().getType().toString().equals("org.apache.uima.SentenceAnnotation"))
			{
				array.add(text.substring(truc.get().getBegin(), truc.get().getEnd()));
			}
			
			truc.moveToNext();
		}
		
			
		return array;
	}
	
}
