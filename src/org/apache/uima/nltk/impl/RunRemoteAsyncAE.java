package org.apache.uima.nltk.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.ProcessTraceEvent;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

/**
 * Example application that calls a Remote Asynchronous Analysis Engine on a
 * collection.
 *
 * This connects to a remote AE at specified brokerUrl and endpoint (which must
 * match what is in the service's deployment descriptor.
 * 
 *   	<p> Requires : 
 *  		<ul> 
 *  			<li>Launching : UIMA_HOME\bin\startBroker</li> 
 *  			<li>Launching : UIMA_HOME\bin\deployAsyncService.sh [endPointRequired].xml -brokerURL tcp://localhost:61616</li> 
 *  		 </ul>
 *  	</p>
 *  
 *  @see CAS
 *  @see Observable
 *  
 *  @author UIMA-NLTK team 
 *  
 *  @version 1.0 
 *  
 */
public class RunRemoteAsyncAE extends Observable {

	/**
	 * Adresse du broker. Fix�e par d�faut.
	 */
	private String brokerUrl = "tcp://localhost:61616";

	/**
	 * File containing collection reader descriptor. Default into folder in project
	 */
	private File collectionReaderDescriptor = new File(
			"descriptors/collection_reader/FileSystemCollectionReaderOurRunRemoteAsyncAE.xml");

	/**
	 * Nombre de CAS que peut contenir l'engine
	 */
	private int casPoolSize = 50;


	private int fsHeapSize = 2000000;


	private int timeout = 0;


	private int getmeta_timeout = 60;
	

	private int cpc_timeout = 0;
	
	/**
	 * Directory containing all files processed
	 */	
	private File outputDir = new File("data/output");

	/**
	 * Saying that we don't want to ignore errors
	 */
	private boolean ignoreErrors = false;


	private boolean logCas = false;

	/**
	 * list of instructions we want to execute trough the text
	 */
	private ArrayList<String> listeInstr;

	/**
	 * Structure containing the status of each instruction to run
	 */
	private HashMap<String, Integer> avancementInstr;

	/**
	 * Start time of the processing - used to compute elapsed time.
	 */
	private static long mStartTime = System.nanoTime() / 1000000;

	/**
	 * List of UimaAsynchronousEngine
	 */
	private ArrayList<UimaAsynchronousEngine> engines = new ArrayList<UimaAsynchronousEngine>();

	/**
	 * Engine's parameters
	 */
	Map<String, Object> appCtx;

	/**
	 * For logging CAS activity
	 */
	private ConcurrentHashMap casMap = new ConcurrentHashMap();

	
	/**
	 * Constructor for the class. Parses command line arguments and sets the
	 * values of fields in this instance. If command line is invalid prints a
	 * message and calls System.exit().
	 * 
	 * @param args
	 *            command line arguments into the program - see class
	 *            description
	 */
	public RunRemoteAsyncAE(ArrayList<String> listeInstructions) throws Exception {
		appCtx = new HashMap<String, Object>();
		appCtx.put(UimaAsynchronousEngine.DD2SpringXsltFilePath,
				System.getenv("UIMA_HOME") + "/bin/dd2spring.xsl");
		appCtx.put(UimaAsynchronousEngine.SaxonClasspath,
				"file:" + System.getenv("UIMA_HOME") + "/saxon/saxon8.jar");
		System.setProperty("defaultBrokerURL", brokerUrl);

		// Création des engines
		for (String instr : listeInstructions) {
			engines.add(new BaseUIMAAsynchronousEngine_impl());
		}
		this.listeInstr = listeInstructions;

		// HashMap qui servira à determiner quel est le prochain engine qu'un
		// CAS doit subir
		// La clef corespond au texte du CAS (je n'ai pas trouvé de meilleur
		// solution pour pouvoir reconnaitre un cas)
		// La valeur corespond à l'index de la liste engines que le CAS vient de
		// subir
		this.avancementInstr = new HashMap<String, Integer>();

	}

	
	/**
	 * Runs the Run Remote Asynchrnous Engine
	 * @throws Exception
	 */
	public void run() throws Exception {
		// add Collection Reader if specified
		System.out.println("Recupération avec Collection Reader");
		CollectionReaderDescription collectionReaderDescription = UIMAFramework
				.getXMLParser().parseCollectionReaderDescription(
						new XMLInputSource(collectionReaderDescriptor));

		CollectionReader collectionReader = UIMAFramework
				.produceCollectionReader(collectionReaderDescription);
		// Le premier engine traite avec le collection Reader (le CAS n'est pas
		// encore présent à ce moment)
		engines.get(0).setCollectionReader(collectionReader);

		// set server URI and Endpoint
		// Add Broker URI
		appCtx.put(UimaAsynchronousEngine.ServerUri, brokerUrl);

		// Add timeouts (UIMA EE expects it in milliseconds, but we use seconds
		// on the command line)
		appCtx.put(UimaAsynchronousEngine.Timeout, timeout * 1000);
		appCtx.put(UimaAsynchronousEngine.GetMetaTimeout,
				getmeta_timeout * 1000);
		appCtx.put(UimaAsynchronousEngine.CpcTimeout, cpc_timeout * 1000);

		// Add the Cas Pool Size and initial FS heap size
		appCtx.put(UimaAsynchronousEngine.CasPoolSize, casPoolSize);
		appCtx.put(UIMAFramework.CAS_INITIAL_HEAP_SIZE,
				Integer.valueOf(fsHeapSize / 4).toString());

		// On ajoute un callback à chaque enggine
		// Et on les initialise tous
		int i = 0;
		for (UimaAsynchronousEngine engine : engines) {
			engine.addStatusCallbackListener(new StatusCallbackListenerImpl());

			// On fait une copie de appCtx auquel on ajoute l'enpoint.
			Map<String, Object> appCtxCpy = new HashMap<String, Object>();
			appCtxCpy.putAll(appCtx);
			appCtxCpy.put(UimaAsynchronousEngine.Endpoint, listeInstr.get(i));
			engine.initialize(appCtxCpy);
			i++;
		}

		// Process sur le 1er engine (c'est le seul à utiliser le collection
		// Reader)
		engines.get(0).process();

	}

	
	/**
	 * Callback Listener. Receives event notifications from CPE. (inner class)
	 * 
	 * 
	 */
	class StatusCallbackListenerImpl extends UimaAsBaseCallbackListener {
		int entityCount = 0;

		long size = 0;

		/**
		 * Called when the initialization is completed.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#initializationComplete()
		 */
		public void initializationComplete(EntityProcessStatus aStatus) {
			if (aStatus != null && aStatus.isException()) {
				System.err.println("Error on getMeta call to remote service:");
				List exceptions = aStatus.getExceptions();
				for (int i = 0; i < exceptions.size(); i++) {
					((Throwable) exceptions.get(i)).printStackTrace();
				}
				System.err.println("Terminating Client...");
				stop();

			}
			System.out.println("UIMA AS Service Initialization Complete");
		}
		

		/**
		 * Stopping
		 */
		private void stop() {
			try {
				for (UimaAsynchronousEngine engine : engines) {
					engine.stop();
				}

			} catch (Exception e) {

			}
			System.exit(1);

		}

		
		/**
		 * Called when the collection processing is completed.
		 * 
		 * @see org.apache.uima.collection.processing.StatusCallbackListener#collectionProcessComplete()
		 */
		public void collectionProcessComplete(EntityProcessStatus aStatus) {

			if (aStatus != null && aStatus.isException()) {
				System.err
						.println("Error on collection process complete call to remote service:");
				List exceptions = aStatus.getExceptions();
				for (int i = 0; i < exceptions.size(); i++) {
					((Throwable) exceptions.get(i)).printStackTrace();
				}
				System.err.println("Terminating Client...");
				stop();
			}
			System.out.print("Completed " + entityCount + " documents");
			if (size > 0) {
				System.out.print("; " + size + " characters");
			}
			System.out.println();
			long elapsedTime = System.nanoTime() / 1000000 - mStartTime;
			System.out.println("Time Elapsed : " + elapsedTime + " ms ");

			// à voir plus tard de ce qu'on en fait ...
			// Remplacer uimaEEEngine par listeEngine(0) ?
			// String perfReport = uimaEEEngine.getPerformanceReport();
			/*
			 * if (perfReport != null) { System.out .println(
			 * "\n\n ------------------ PERFORMANCE REPORT ------------------\n"
			 * ); System.out.println(uimaEEEngine.getPerformanceReport()); }
			 */
			// stop the JVM.
			// stop();
		}

		
		/**
		 * Called when the processing of a Document is completed. <br>
		 * The process status can be looked at and corresponding actions taken.
		 * 
		 * @param aCas
		 *            CAS corresponding to the completed processing
		 * @param aStatus
		 *            EntityProcessStatus that holds the status of all the
		 *            events for aEntity
		 */
		public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
			
			String clef = aCas.getDocumentText();
			boolean stop = true;
			int val;
			// Si la clef (qui corespond au texte du CAS n'est pas présent dans
			// la HashMap)
			if (!avancementInstr.containsKey(clef)) {
				// L'engine numéro 0 vient d'être effectué, on passe donc au
				// numéro 1
				avancementInstr.put(new String(aCas.getDocumentText()), 1);
				val = 1;
				// Sinon on passe à l'engine suivant
			} else {
				val = avancementInstr.get(clef) + 1;

				avancementInstr.put(clef, val);
			}
			// Si c'est n'est pas le dernier traitement qui vient d'être réalisé
			// on met stop à false
			if (engines.size() > val) {
				stop = false;
			}

			if (aStatus != null) {

				if (aStatus.isException()) {
					System.err
							.println("Error on process CAS call to remote service:");
					List exceptions = aStatus.getExceptions();
					for (int i = 0; i < exceptions.size(); i++) {
						((Throwable) exceptions.get(i)).printStackTrace();
					}
					if (!ignoreErrors) {
						System.err.println("Terminating Client...");
						stop();
					}
				}

				if (logCas) {
					String ip = "no IP";
					List eList = aStatus.getProcessTrace()
							.getEventsByComponentName("UimaEE", false);
					for (int e = 0; e < eList.size(); e++) {
						ProcessTraceEvent event = (ProcessTraceEvent) eList
								.get(e);
						if (event.getDescription().equals("Service IP")) {
							ip = event.getResultMessage();
						}
					}
					String casId = ((UimaASProcessStatus) aStatus)
							.getCasReferenceId();
					if (casId != null) {
						long current = System.nanoTime() / 1000000 - mStartTime;
						if (casMap.containsKey(casId)) {
							Object value = casMap.get(casId);
							if (value != null && value instanceof Long) {
								long start = ((Long) value).longValue();
								System.out.println(ip + "\t" + start + "\t"
										+ (current - start));
							}
						}
					}

				} else {
					System.out.print(".");
					if (0 == (entityCount + 1) % 50) {
						System.out.print((entityCount + 1) + " processed\n");
					}
				}
			}

			// System.out.println(listOfWords.toString());

			// update stats
			entityCount++;
			String docText = aCas.getDocumentText();
			if (docText != null) {
				size += docText.length();
			}

			// Si non stop, on envoie le CAS à l'engine
			if (!stop) {
				UimaAsynchronousEngine engine = engines.get(avancementInstr
						.get(aCas.getDocumentText()));
				// On créé une copie du CAS que la méthode EntityProcessComplete a reçu en paramètre
				CAS cas = copyCas(aCas);
				try {
					//On envoie le Cas pour qu'il soit traité
					engine.sendCAS(cas);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			//Si c'est la fin
			}else{
				//Utilisation du design pattern observer pour envoyer le CAS final (avec toute les annotations demandés) au Pipeline
				setChanged();
				notifyObservers(aCas);
				
			}

		}
		
		
		/**
		 * Copying a CAS using XML powerful technologies (lol)
		 * @param cas
		 * @return copy of cas
		 */
		public CAS copyCas(CAS cas){
			UimaAsynchronousEngine engine = engines.get(avancementInstr
					.get(cas.getDocumentText()));
			File outFile = new File(outputDir, "doc" + entityCount);

			try {
				FileOutputStream outStream = new FileOutputStream(outFile);
				try {
					XmiCasSerializer.serialize(cas, outStream);
				} finally {
					outStream.close();
				}
			} catch (Exception e) {
				System.err.println("Could not save CAS to XMI file");
				e.printStackTrace();
			}

			// Deserialiser le type du systeme
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(outFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final XMLInputSource xmlIn = new XMLInputSource(inputStream,
					null);

			// Deserialiser le CAS
			CAS newCas = null;
			try {
				newCas = CasCreationUtils.createCas(engine.getMetaData());
			} catch (ResourceInitializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				XmiCasDeserializer.deserialize(inputStream, newCas, true);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return newCas;
		}

		

		public void onBeforeMessageSend(UimaASProcessStatus status) {
			long current = System.nanoTime() / 1000000 - mStartTime;
			casMap.put(status.getCasReferenceId(), current);
		}
		

		/**
		 * This method is called when a CAS is picked up by remote UIMA AS from
		 * a queue right before processing. This callback identifies on which
		 * machine the CAS is being processed and by which UIMA AS service
		 * (PID).
		 */
		public void onBeforeProcessCAS(UimaASProcessStatus status,
				String nodeIP, String pid) {

		}

	}

}
