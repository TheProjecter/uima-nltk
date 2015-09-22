### Avertissement ###
  * Ci-dessous remplacer /chemin/vers/mon/apache-uima-as-2.4.0 par le chemin vers votre répertoire d'install du bin
  * Ai travaillé avec le bin  UIMA-AS Version 2.4.0 Release Date: 15-Nov-2012 disponible ici http://uima.apache.org/downloads.cgi

### A ma disposition pour prendre en main UIMA AS ###
  * le README à la racine du bin
  * les codes sources des différentes classes invoquées par les scripts du bin
  * la doc qui explique la syntaxe des descripteurs de déployement de services et l'API UIMA AS pour développer des applications qui utilisent ces services

# ci-dessous les chemins en ligne de ceux que j'ai observé
  * https://svn.apache.org/repos/asf/uima/uima-as/trunk/README
  * https://svn.apache.org/repos/asf/uima/uima-as/trunk/uimaj-as-activemq/src/main/java/org/apache/uima/examples/as/RunRemoteAsyncAE.java
  * à la racine du bin docs/d/uima\_async\_scaleout.pdf

### D'après 2.2. Environment Variables Settings ###
# Terminal 1

# export JAVA\_HOME=...

```
cd /chemin/vers/mon/apache-uima-as-2.4.0
export UIMA_HOME=$(pwd)
export PATH=$PATH:$UIMA_HOME/bin
chmod a+rx bin/*.sh
```

### 3.4 Quick Test of an async service ###
J'exécute le broker
```
mkdir some-writable-directory
cd some-writable-directory/
startBroker.sh
```

  * Terminal 2 (after setting the environment variables)
  * j'observe que des fichiers de log et de conf ont été créées suite à l'exécution du broker
`cat amq/activemq-data/activemq.log`

  * je poursuis le test
```
cd $UIMA_HOME/examples/deploy/as
runRemoteAsyncAE.sh tcp://localhost:61616 MeetingDetectorTaeQueue -d Deploy_MeetingDetectorTAE.xml -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml
```

  * output
```
java.lang.reflect.InvocationTargetException
Caused by: org.apache.uima.resource.ResourceInitializationException: Invalid value for parameter "InputDirectory" in component "File System Collection Reader" -- directory "C:/Program Files/apache-uima/examples/data" does not exist.
```

  * une valeur invalide
  * je stop the process CTR+C

### Spécifier l'entrée d'une chaîne UIMA AS (cad les documents à traiter) ###
  * Je lis le README
  * en particulier 3.3 Calling a UIMA AS Asynchronous Service (via le script runRemoteAsyncAE.sh)
  * "-c  Specifies a CollectionReader descriptor.  The client will obtain CASes from the CollectionReader and send them to the service for processing."
  * Le descripteur du CR doit spécifier un chemin réel vers un répertoire de données à traiter (par exemple des textes brutes)
  * Il y en a dans le répertoire examples/data
  * je mets à jour le desc du CR
```
gedit $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml
```

  * je change


> 

&lt;nameValuePair&gt;


> > 

&lt;name&gt;

InputDirectory

&lt;/name&gt;


> > 

&lt;value&gt;


> > > 

&lt;string&gt;

C:/Program Files/apache-uima/examples/data

&lt;/string&gt;



> > 

&lt;/value&gt;



> 

&lt;/nameValuePair&gt;




en


> 

&lt;nameValuePair&gt;


> > 

&lt;name&gt;

InputDirectory

&lt;/name&gt;


> > 

&lt;value&gt;


> > > 

&lt;string&gt;

/chemin/vers/mon/apache-uima-as-2.4.0/examples/data

&lt;/string&gt;



> > 

&lt;/value&gt;



> 

&lt;/nameValuePair&gt;




  * Rerun
```
runRemoteAsyncAE.sh tcp://localhost:61616 MeetingDetectorTaeQueue -d Deploy_MeetingDetectorTAE.xml -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml
```
  * output
```
Attempting to deploy Deploy_MeetingDetectorTAE.xml ...
Service:Meeting Detector TAE Initialized. Ready To Process Messages From Queue:MeetingDetectorTaeQueue
UIMA AS Service Initialization Complete
........Completed 8 documents; 17165 characters
Time Elapsed : 3728 ms 
.................... Thread:11 CountDownLatch Value:0
....... AnalysisEngineInstancePool.destroy() was called
```

j'ai l'impression qu'un traitement a été exécuté (initialization, completed 8 docs, pool.destroy)

mais ne sais pas si un export du résultat du traitement a eu lieu et où ?

### Récupérer la sortie d'un traitement UIMA AS ###
Je poursuis ma lecture
3.3 Calling a UIMA AS Asynchronous Service (via le script runRemoteAsyncAE.sh)

  * Après -c  Specifies a CollectionReader descriptor et -d  Specifies a deployment descriptor
  * (On notera DD ce dernier descripteur)
  * Je lis aussi dans la continuité
  * -o  Specifies an Output Directory.  All CASes received by the client's  CallbackListener will be serialized to XMI in the specified OutputDir. If omitted, no XMI files will be output.
  * ...

JeRerun
```
mkdir /tmp/output
runRemoteAsyncAE.sh tcp://localhost:61616 MeetingDetectorTaeQueue -d Deploy_MeetingDetectorTAE.xml -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml -o /tmp/output
```

le répertoire /tmp/output contient bien le résultats au format XMI des traitements opérés sur les documents passés en entrée.
Par exemple
`less /tmp/output/Apache_UIMA.txt.xmi`

j'en déduis que dans le code de runRemoteAsyncAE il doit y avoir une référence au CAS qui résulte du traitement

### Quand/comment se fait le déploiement des services ? ###
Il y a des choses que je ne comprends pas :
runRemoteAsyncAE.sh exécute un descripteur de déploiement (via le param -d) qui n'est pas le descripteur d'un AE.

Je relis le README
  * La section "3.2 Deploying an Analysis Engine as a UIMA AS Asynchronous Service" m'apprend la particularité de ce descripteur et où il y a une doc pour connaître la syntaxe ainsi que où je peux trouver quelques exemples (l'exemple exécuté par runRemoteAsyncAE en fait partie)
  * La section présente aussi le script   deployAsyncService.sh
  * Je m'interroge sur le rôle du script car je n'ai pas moi-même déployé de services sauf via le runRemoteAsyncAE
  * Je relis le README à propos du runRemoteAsyncAE.sh
  * Je note que les descripteurs de CR et DD sont optionnels et que par contre deux sont obligatoires : brokerUrl and endpoint
  * la doc indique que le endpoint "must match the inputQueue endpoint in the remote AE service's deployment descriptor"
  * grosso modo ca doit être un nom qui se trouve dans le DD déployé...
  * Je vérifie
grep MeetingDetectorTaeQueue Deploy\_MeetingDetectorTAE.xml
  * Exact
  * Je vois mieux l'usage du script   deployAsyncService.sh
  * on déploit des DD avec deployAsyncService.sh à qui on indique aussi un broker
  * et on invoque les DD via le endpoint communiqué à runRemoteAsyncAE.sh à qui on indique aussi le chemin du broker
  * J'expérimente

### Déployer un service indépendamment du runRemoteAsyncAE ? ###

Je déploie
```
deployAsyncService.sh Deploy_MeetingDetectorTAE.xml -brokerURL tcp://localhost:61616
```

output
```
>>> Setting defaultBrokerURL to:tcp://localhost:61616
Service:Meeting Detector TAE Initialized. Ready To Process Messages From Queue:MeetingDetectorTaeQueue
Press 'q'+'Enter' to quiesce and stop the service or 's'+'Enter' to stop it now.
Note: selected option is not echoed on the console.
```
il me semble en attente

Terminal 3 (after setting the environment variables)
Je rerun sans préciser de DD
```
rm /tmp/output/*
runRemoteAsyncAE.sh tcp://localhost:61616 MeetingDetectorTaeQueue  -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml -o /tmp/output
```

output
```
UIMA AS Service Initialization Complete
........Completed 8 documents; 17165 characters
Time Elapsed : 2137 ms 
```
Moins verbeux mais ne se chargeait pas du déploiement de services donc ca doit être normal.
Il semble avoir opéré le traitement demandé car des fichiers résultats sont présents (ls /tmp/output/)

#
date
# mercredi 23 janvier 2013, 14:42:11 (UTC+0100)
# ca fait une heure que j'ai quitté mes étudiants et c'est le temps que ca m'a pris pour le même résultat qu'eux en un mois...
# va falloir vous réveiller les gars !!!!



### Comment fonctionne le RunRemoteAsyncAE ###

  * Je lis le code RunRemoteAsyncAE.java pour connaître le déroulement de la classe
  * 'identifie le main, le constructeur qui parse les paramètres, et la méthode run qui exécute une chaîne de traitement
  * e tombe sur une méthode entityProcessComplete(CAS aCas, EntityProcessStatus aStatus)
  * Called when the processing of a Document is completed."
  * etourne le CAS du document traité et un status sur la réussite du traitement
  * je ne sais pas trop comment elle est appelée mais c'est elle qui gère l'export des données via le param -o


Un oeil dans la doc me semble nécessaire sur le format du desc DD
  * Je consulte docs/d/uima\_async\_scaleout.pdf.
  * on est pas sortie de l'auberge... 60 pages
  * je survole en 10 minutes (toi aussi tu peux le faire) :
  * au chap1 des concepts généraux et importants,
  * au chap 2 la description du descripteur DD (les paramères optionnels ne me semblent pas nécessaire pour nous),
  * au chap 3 la présentation de l'API UIMA AS (celle utilisée dans runRemoteAsyncAE) très importants !!!

En particulier l'API de UimaAsynchronousEngine dont on trouve la javadoc en ligne (v 3.0)
http://uima.apache.org/downloads/releaseDocs/2.3.0-incubating/docs-uima-as/api/index.html



### Création de 2 services et on verra les problèmes qui se posent ###

Nous allons créer 2 services. Le premier qui découpe à la fois en phrases et mots  et le second qui étiquette grammaticalement les mots. Le premier est pris en charge par le  composant uima addons WhitespaceTokenizer et le second par uima addons tagge.

  * Récupération des uima addons http://uima.apache.org/downloads.cgi
(seulement la version 2.3.1 disponible en bin)
  * Copie du répertoire  `addons` contenu dans le répertoire  `apache-uima` de l'archive dans le répertoire UIMA\_HOME
  * Initialisation du classpath
```
export CLASSPATH=$(pwd)/addons/annotator/WhitespaceTokenizer/lib/uima-an-wst.jar:$(pwd)/addons/annotator/Tagger/lib/uima-an-tagger.jar 
```
Ces jars contiennent à la fois les desc AE, les implémentations et les ressources requises pour analyser de l'anglais...

A la main j'édite 2 desc DD qui référence les desc des AE.
J'ai tenté dans un premier temps de les déclarer by name et de mettre le jar dans le classpath mais j'ai obtenu le message suivant lors du déploiement
```
>>> Setting defaultBrokerURL to:tcp://localhost:61616
Error on line 2381 of file:/home/hernandez/local/applications/apache-uima-as-2.4.0/bin/dd2spring.xsl:
  FODC0005: java.io.FileNotFoundException:
  /home/hernandez/local/applications/apache-uima-as-2.4.0/bin/ERROR converting import by
  name to absolute path (Aucun fichier ou dossier de ce type)
```
Finalement donc sur le modèle de Deploy\_MeetingDetectorTAE.xml
  * examples/deploy/as/WhitespaceTokenizerLocationDD.xml
  * examples/deploy/as/HMMPOSTaggerLocationDD.xml

Consulter la section _Downloads_ de cette forge

```
# term 1
export UIMA_HOME=$(pwd)
export PATH=$PATH:$UIMA_HOME/bin
mkdir some-writable-directory
cd some-writable-directory/
startBroker.sh
#
# term 2
# CTRL + SHIFT + T
cd ..
export UIMA_HOME=$(pwd)
export PATH=$PATH:$UIMA_HOME/bin
export CLASSPATH=$(pwd)/addons/annotator/WhitespaceTokenizer/lib/uima-an-wst.jar:$(pwd)/addons/annotator/Tagger/lib/uima-an-tagger.jar 
cd examples/deploy/as/
deployAsyncService.sh WhitespaceTokenizerLocationDD.xml -brokerURL tcp://localhost:61616
#
# term 3
# CTRL + SHIFT + T
cd ../../../
export UIMA_HOME=$(pwd)
export PATH=$PATH:$UIMA_HOME/bin
export CLASSPATH=$(pwd)/addons/annotator/WhitespaceTokenizer/lib/uima-an-wst.jar:$(pwd)/addons/annotator/Tagger/lib/uima-an-tagger.jar 
cd examples/deploy/as/
deployAsyncService.sh EnPOSTaggerLocationDD.xml -brokerURL tcp://localhost:61616
# 
# term 4
# CTRL + SHIFT + T
export UIMA_HOME=$(pwd)
export PATH=$PATH:$UIMA_HOME/bin
mkdir /tmp/output/
rm /tmp/output/*
```
```
runRemoteAsyncAE.sh tcp://localhost:61616 WhitespaceTokenizerQueue -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml -o /tmp/output
```
`less /tmp/output/Apache_UIMA.txt.xmi`
contient des annotations de segmentation en mots
```
runRemoteAsyncAE.sh tcp://localhost:61616 EnPOSTaggerQueue -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml -o /tmp/output
```
`less /tmp/output/Apache_UIMA.txt.xmi`
ne contient aucune annotation ; ce qui est normal, car normalement le tagger repose sur la segmentation en mots.

Je peux faire un AAE mais ca ne résout pas notre problème au niveau de l'interface uima-nltk sauf à générer dynamiquement cet AAE... ce n'est pas la solution car ca voudrait dire qu'on doit aussi générer au niveau des delegates les aspects as... Or on partait dans l'idée qu'il y avait un gentil admin qui déployait les services pour nous et qu'on avait ensuite plus qu'à les utiliser...

Autres expériences (j'arrête les services déployés)
```
#
runRemoteAsyncAE.sh tcp://localhost:61616 EnPOSTaggerQueue -d WhitespaceTokenizerLocationDD.xml -d EnPOSTaggerLocationDD.xml -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml -o /tmp/output
#
runRemoteAsyncAE.sh tcp://localhost:61616 WhitespaceTokenizerQueue -d WhitespaceTokenizerLocationDD.xml -d EnPOSTaggerLocationDD.xml -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml -o /tmp/output
#
runRemoteAsyncAE.sh tcp://localhost:61616 EnPOSTaggerQueue  -d EnPOSTaggerLocationDD.xml -d WhitespaceTokenizerLocationDD.xml -c $UIMA_HOME/examples/descriptors/collection_reader/FileSystemCollectionReader.xml -o /tmp/output
```

ne marche pas mieux...

  * doit on à la main générer tous les desc AAE et DD qui vont bien ?
  * peut on définir que des DD primitifs et gérer des endpoints dans l'appli et créer le UimaAsynchronousEngine selon ce qui est demandé par l'API en renvoyant les CAS résultant d'un traitement antérieur par un autre UimaAsynchronousEngine ?
  * il faudrait voir des exemples avec sendCAS...
  * et quel tête aurait le programme qu'on écrirait avec l'API ?



L'API après  avoir jété un oeil sur http://www.ibm.com/developerworks/linux/library/l-cpnltk/index.html pour imaginer un programme à reproduire
```
List<String> textCollection = loadTextCollectionFromFileSystem(chemin);
for (String text : textCollection) {
  //String text = "Ceci est une phrase. Et là une seconde.";
  List<String> sentencesList =  tokenizeSentences(text);
  for (String sentence : sentencesList) {
    List<String> wordsList = tokenizeWords(sentence);
    List<List<String>> wordTagsList = posTag(wordsList);
    // nlkt uses tuples to represent (a kind of list) the association of a word with its tag
  }
}
```

  * loadTextCollectionFromFileSystem
  * tokenizeSentences
  * tokenizeWords
  * posTag

**TODO** L'implémentation...
  * autant de méthode que UimaAsynchronousEngine créée et de service déployée ?
  * avec à chaque premier appel de la méthode une instance de   UimaAsynchronousEngine créée ?
  * les méthodes  manipulent quoi ? CAS ? Une signature différente pour manipuler des formats simples consensuels ?
  * une écriture sur disque des XMI entre chaque méthode ? non il ne faut pas ! il faut récupérer les CAS



**TODO** A APPROFONDIR

### Comment tout cela est actuellement interconnecté et comment faire apparaître ce que demande le prof ###
Va falloir une pose pour digérer tout cela
> comment relier
    * Interface UIMA NLTK
    * Implémentation
    * CR
    * DD
    * AE
    * deployAsyncService
    * runRemoteAsyncAE

Début de la réflexion :

  * Un AE correspond à un traitement métier (e.g. tokenizeWords), cela comprend un desc xml et du code java compilé, se présente en général le tout dans un jar ; ce jar doit se trouver dans le CLASSPATH. Un AE peut réaliser plusieurs traitements métiers (le descripteur fait référence alors à plusieurs autres descripteurs qui viennent avec leur implémentation)
  * un DD est un desc xml qui référence un AE et qui est déployé par deployAsyncService ; il est référencable par un endpoint qu'il définit dans le desc

  * Actuellement nous avons des AE pour un segmenteur en phrases (tokenizeSentences), un tokenizer en mots (tokenizeWords) et un étiqueteur grammatical (posTag)
  * il est relativement "simple" par mimétisme ou en lisant la doc (cf. plus haut) de produire les DD correspondants

**TODO** A suivre

### Comment déployer plusieurs services avec des interconnexions entre eux ? ###

  * on peut ajouter autant de DD à un UimaAsynchronousEngine, mais on spécifie qu'un seul endpoint dans l'applicationContext (appCtx)
  * est ce que c'est dans le DD correspondant à ce endpoint que l'on fait le lien avec les autres services ?
  * après une brève relecture de chap 2 de docs/d/uima\_async\_scaleout.pdf, il est possible de surcharger la définition de composant d'un aggregate (AAE) référencé comme top dans le DD (on peut spécifier qu'il est distant par exemple sur un autre broker)
  * est ce alors au niveau d'un desc d'AAE que l'on spécifie l'ordre (le flow) et les composants que l'on souhaite utiliser ?
  * Doit on créer plusieurs UimaAsynchronousEngine pour chaque étape d'un traitement TAL (i.e. segmentation en phrases, en mots, en étiquette gram) ?
UimaAsynchronousEngine offre 3 moyens d'exécution (voir la javadoc) :
  * For synchronous processing an application should call #sendAndReceive(CAS) method.
  * For asynchronous processing the application should call sendCAS(CAS) method.
  * Additionally, processing with a client side CollectionReader is supported as well. The application first instantiates and initializes the CollectionReader and registers it via #setCollectionReader(CollectionReads) method. Once the CollectionReader is registered, and initialize(Map) method is called, the application may call process() method.
Ainsi suivant la disponibilité d'un document (nouveau ou déjà traité) on utiliserait le choix 3 ou le choix 2 sur des UimaAsynchronousEngine  créés ?


### Conclusion ###

En l'état, j'ai l'impression que la solution la plus simple est de considérer chaque méthode métier de l'API NLTK comme un UimaAsynchronousEngine. En d'autres mots, le code du runRemoteAsyncAE constitue le code de chaque méthode.
En factorisant, le endpoint devient ce qui distingue chaque méthode...
On sait que l'on peut diriger l'import et l'export de données à traiter/traités sur le file system.
De la même manière ca veut dire qu'on pourrait choisir une autre forme de persistence plus rapide (base de données).
Est il possible de récupérer le CAS et de le renvoyer sur un autre UimaAsynchronousEngine sans persistence.
Par ailleurs ca veut dire qu'au niveau des méthodes il y a des status d'exécution à gérer (tous les documents sont ils traités .

Questions :
  * les AE traitent des textes, or certaines méthodes traitent des phrase ou des mots, ca me semble coûteux de lancer un AsE (UimaAsynchronousEngine) pour cela..., comment spécifier des parties du CAS car pour l'instant on manipule seulement le CAS.
  * comment renvoyer des CAS sur un autre UimaAsynchronousEngine voir la javadoc et chercher des exemples si possible

au niveau de la mise en oeuvre deux possibilités ; soit à l'appel de la méthode ont fait le traitement, soit on définit les traitements à réaliser, ils le sont tous et on récupère ensuite les résultats...
un peu comme http://nlp.stanford.edu/software/corenlp.shtml (voir l exemple de code au bas de la page) ; cette dernière solution semble plus simple à mettre en oeuvre.