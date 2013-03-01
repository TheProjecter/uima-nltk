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
		FSIterator<AnnotationFS> iter = annotIndex.iterator();
		
		
		
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
	
	
	
	
	
	
	
	
	public static ArrayList<String> fromCas2ArrayString4Sentence(CAS aCas)
	{
		ArrayList<String> array = new ArrayList<String>();
				
		String text = aCas.getDocumentText();
		
		AnnotationIndex<AnnotationFS> annotIndex = aCas.getAnnotationIndex();
		FSIterator<AnnotationFS> iter = annotIndex.iterator();
		
		
		
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
	
	public static ArrayList<ArrayList<String>> fromCas2ArrayString4Postag(CAS aCas)
	{
		ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
				
		String text = aCas.getDocumentText();
		
		AnnotationIndex<AnnotationFS> annotIndex = aCas.getAnnotationIndex();
		FSIterator<AnnotationFS> iter = annotIndex.iterator();
		
		
		
		while(iter.hasNext())
		{
			if(iter.get().getType().toString().equals("org.apache.uima.TokenAnnotation"))
			{
				ArrayList<String> temp = new ArrayList<String>();
				
				
				
				temp.add(text.substring(iter.get().getBegin(), iter.get().getEnd()));
				array.add(temp);
			}
			
			iter.moveToNext();
		}
		
			
		return array;
	}
}
