package org.apache.uima.nltk.impl;

import java.util.Scanner;

public class TestPipeline {

	public static void main(String args[]) throws Exception{
		
		Scanner lectureClavier1 = new Scanner(System.in);
		Scanner lectureClavier2 = new Scanner(System.in);

		
		UimaNltk uima = new UimaNltk();
		
		try {
			uima.loadText();
		} catch (Exception e) {
			System.err.println("     ERROR  WITH UIMA ENGINE ");
			e.printStackTrace();
		}
	
			
		long timeNow = System.currentTimeMillis();
		while(System.currentTimeMillis()-timeNow<1000)
		{
			
		}
		
		System.out.println(" ============= WELCOME TO UIMA4NLPISTS APPLICATION ============= \n");
		System.out.println("    This is an application (without graphic HCI) using Java API \"uima-nltk\". \n");
		System.out.println("\n\n\n");
		System.out.println("    1 : Follow");
		System.out.println("    9 : Exit");
		System.out.println(">>> ");


		//READ ENTRY
		int choix1 = lectureClavier1.nextInt();
		while(choix1 != 9)
		{

			//IF 2
			System.out.println("\n");
			System.out.println("    Please, select an operation. The application will process all files given in the \"input\" directory (see README for more details) ");
			System.out.println("    	3 operations : ");
			System.out.println("    		- word_tokenize() => may return ['a','piece','of','text.']");
			System.out.println("    		- sentence_tokenize() => may return ['my name is X.','his name is Y.']");
			System.out.println("    		- pos_tag() => may return [('a','IN'),('piece','NN'),('of','IN'),('text.','NN')]");
			System.out.println("    >>> ");
			String choix2 = lectureClavier2.nextLine();
			
			if(choix2.startsWith("word_token"))
			{
				System.out.println(uima.getWordTokens());
			}
			else if(choix2.startsWith("sentence_token"))
			{
				System.out.println(uima.getSentenceTokens());
			}
			else if(choix2.startsWith("pos_tag"))
			{
				System.out.println(uima.getTagTokens());
			}
			else
			{
				System.err.println("    [err] Unknown Operation ");
			}

	
			System.out.println("\n\n\n");
			System.out.println("    1 : Follow");
			System.out.println("    9 : Exit");
			System.out.println(">>> ");
			choix1 = lectureClavier1.nextInt();
		}
	}
}
