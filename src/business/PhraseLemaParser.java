package business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * @author Grupo 02 - João Gonçalves - número 68041 
 *
 */

public class PhraseLemaParser {

	private HashMap<String, ParsedPhrase> phrasesProbabilities;
	public String sresult = null;
	private static HashMap<String, Integer> hmunigram = new HashMap<String, Integer>();
	private static HashMap<String, Integer> hmbigram = new HashMap<String, Integer>(); ;
	private static String ambiguousformregex;
	private static List<String> llemas = new ArrayList<String>();
	private static int nTokens;
	
	public PhraseLemaParser(String[] phrases, Scanner sunigram, Scanner sbigram, Scanner slemaparameterization){
	
		this.phrasesProbabilities = new HashMap<String, ParsedPhrase>();
		
		/*
		 * creates a HashMap with all the unigrams and bigrams retrieved from the correspondent Scanner files to use their values to calculate MLE probabilities
		 */
		mapGramScanner(sunigram, "unigram");
		mapGramScanner(sbigram, "bigram");
		
		/*
		 * parses the parameterization input file and put all the lemas on a List
		 */
		storeLemas(slemaparameterization);
		
		for (String onePhrase : phrases) {
			this.phrasesProbabilities.put(onePhrase, processOnePhrase(onePhrase));
		}
		
		this.sresult = toString();		
		
		System.out.println(this.sresult);
	}
	
	/*
	 * processes each sentence for each of the lema word to consider and determines the sentence probability
	 */
	private ParsedPhrase processOnePhrase(String phrase){
		
		int vTokens = 0;
		
		ParsedPhrase oneParsedPhrase = new ParsedPhrase();
		oneParsedPhrase.phraseDatas = new ArrayList<PhraseData>();
		
		if (llemas.size() > 1){

			phrase = "<s> " + phrase + " </s>"; 
			
			Iterator<String> iterator = llemas.iterator();
			
			PhraseData onePhraseData = null;
			
			/*
			 * controls is the MLE probability can be calculated without smoothing
			 * if it is needed to use smoothing for a lema, then with will be used for all for that sentence
			 */			
			boolean unsmoothed = true;
			
			Set<String> vTokenWords = new HashSet<String>();
			
			while (iterator.hasNext()) {

				Double unsmoothedPphrase = null;
				Double smoothedPphrase = null;
				
				List<String> phraseBigrams = new ArrayList<String>();
				List<String> phraseUnigrams = new ArrayList<String>();
				
				onePhraseData = replaceAmbiguousWithLema(iterator.next(), phrase);
				
				String phraseWords[] = onePhraseData.phrasewithlema.split(" ");
				
				for (int i = 0; i < phraseWords.length; i++){
					
					/*
					 * verifies if each word of the sentence exists on the unigram file
					 * if not, then the number of word (V) needs to be incremented
					 */
					if (nGramExists(phraseWords[i], 1) == false){
						if (!vTokenWords.contains(phraseWords[i])){
							vTokens += 1;
							vTokenWords.add(phraseWords[i]);
						}						
						
						/*
						 * if the unigram doesn't exists on the unigram file, then there is no use to calculate the probability for the sentence without smoothing becaus it is going to be 0
						 */						
						unsmoothed = false;
					}
					
					/*
					 * adds the unigram to the list of the unigrams of the sentence
					 */
					phraseUnigrams.add(phraseWords[i]);
					
					if (i != phraseWords.length -1 ){
						if (nGramExists(phraseWords[i] + " " + phraseWords[i+1], 2)){
							//se o bigrama não existir no corpus não vale a pena calcular a probabilidade sem smoothing porque vai ser = 0
							unsmoothed = false;
						}
						/*
						 * adds the bigram to the list of the unigrams of the sentence
						 */
						phraseBigrams.add(phraseWords[i] + " " + phraseWords[i+1]);
					}				
					
				}

				for (String oneBigram : phraseBigrams) {
					
					if (unsmoothed == true){
						if (unsmoothedPphrase != null){
							unsmoothedPphrase *= (double)calcBigramProbability(oneBigram, false, PhraseLemaParser.nTokens);
						}else{
							unsmoothedPphrase = (double)calcBigramProbability(oneBigram, false, PhraseLemaParser.nTokens);
						}
						
					}				
					
					if (smoothedPphrase != null){
						smoothedPphrase *= (double)calcBigramProbability(oneBigram, true, PhraseLemaParser.nTokens + vTokens);
					}else{
						smoothedPphrase = (double)calcBigramProbability(oneBigram, true, PhraseLemaParser.nTokens + vTokens);
					}				
					
				}
				
				if (unsmoothedPphrase != null){
					onePhraseData.probability = unsmoothedPphrase;
					onePhraseData.smoothed = false;
				}else if (smoothedPphrase != null){
					onePhraseData.probability = smoothedPphrase;
					onePhraseData.smoothed = true;
				}
				
				oneParsedPhrase.phraseDatas.add(onePhraseData);
				oneParsedPhrase.unsmoothed = unsmoothed;
				
			}
			
			/*
			 * validates the highest probability for the sentence, and the highest one defines which lema is the most probable
			 */
			String mostProbLema = null;
			Double highestProb = 0.0;
			
			for (PhraseData phraseData : oneParsedPhrase.phraseDatas) {
				int retVal = phraseData.probability.compareTo(highestProb);
				if (retVal > 0){
					highestProb = phraseData.probability;
					mostProbLema = new String(phraseData.lema);
				}
				
			}
			
			oneParsedPhrase.mostProbLema = mostProbLema;
			oneParsedPhrase.probability = highestProb;

		}

		return oneParsedPhrase;
		
	}
	
	private void mapGramScanner(Scanner ngramScanner, String type){
		
		String[] onengram;
		while (ngramScanner.hasNextLine()) {
			onengram = ngramScanner.nextLine().split("\t");
			if(type.equals("unigram")){
				if(onengram.length == 2){
					PhraseLemaParser.nTokens += 1; 
					hmunigram.put(onengram[0], Integer.parseInt(onengram[1]));
				}	
			}else if (type.equals("bigram")){
				if(onengram.length == 2){
					hmbigram.put(onengram[0], Integer.parseInt(onengram[1]));
				}
			}
	    }
		
	}
	
	/*
	 * method that creates the regex expression for the ambiguous word to replace on each sentence
	 */
	private String getWordRegex(String ambiguousword) {
			
		String regexanychar = new String("[^a-z,.,;,\"]*");
		
		String regcorpus = regexanychar;
		
		for (int i = 0; i < ambiguousword.length(); i++){
			regcorpus += "[" + (String)(ambiguousword.charAt(i)+"").toLowerCase() + "|" + (String)(ambiguousword.charAt(i)+"").toUpperCase() + "]";
		}
		
		regcorpus += regexanychar;
		
		return regcorpus;
			
	}
	
	/*
	 * verifies if the ngram exists
	 */
	private boolean nGramExists(String ngram, int gramDimension){
		
		boolean itExists = false;
		
		if (gramDimension == 1){
			if (hmunigram.containsKey(ngram)){
				itExists = true;
			}
		}else if (gramDimension == 2){
			if (hmbigram.containsKey(ngram)){
				itExists = true;
			}
		}
		
		return itExists;
		
	}
	
	/*
	 * calculates the bigram probability
	 */
	private double calcBigramProbability(String bigram, boolean smoothed, int nvTokens){
		
		double bigramProbability = 0;
		
		String[] splitedbigram = bigram.split(" ");
		
		if(splitedbigram.length == 2){
			
			if(smoothed == false){
				bigramProbability = (double)(hmbigram.get(bigram)) / (double)(hmunigram.get(splitedbigram[0]));
			}else{
				
				double dividend = 0;
				double divisor = 0;
				
				if (nGramExists(bigram, 2)){
					dividend = (double)(hmbigram.get(bigram) + 1);
				}else{
					dividend = 1;
				}
				
				if (nGramExists(splitedbigram[0], 1)){
					divisor = (double)(hmunigram.get(splitedbigram[0]) + nvTokens);
				}else{
					divisor = nvTokens;
				}
				
				bigramProbability =  (double)dividend/(double)divisor;
			}
			
		}

		return bigramProbability;
		
	}
	
	/*
	 * stores the lemas words based on the parameterization file
	 */	
	private void storeLemas(Scanner slemaparameterization){
		
		while (slemaparameterization.hasNextLine()) {
			String[] oneline = slemaparameterization.nextLine().split(" ");
			if(oneline.length == 1){
				ambiguousformregex = getWordRegex(oneline[0]);
			}else if (oneline.length > 1){
				for (String onelema : oneline) {
					llemas.add(onelema.toUpperCase());
				}
			}			
	    }
		
	}
	
	/*
	 * replaces the ambiguous word on the sentence with the lema received
	 */
	private PhraseData replaceAmbiguousWithLema(String lema, String phrase){
		
		PhraseData onePhraseData = new PhraseData(lema, phrase.replaceAll(ambiguousformregex, " " + lema + " "));
				
		return onePhraseData;
		
	}
	
	@Override
	public String toString(){
		
		String plpString = new String("");
		
		Set<String> keySet = this.phrasesProbabilities.keySet();
		Iterator<String> keySetIterator = keySet.iterator();
		
		int i = 1;
		
		while (keySetIterator.hasNext()){
			
			String key = keySetIterator.next();
			
			plpString += "Frase " + i + " : " + key + "\n\n"; 
			
			ParsedPhrase oneParsedPhrase = this.phrasesProbabilities.get(key);
			
			plpString += "\t\tLema mais provável: " + oneParsedPhrase.mostProbLema + "\n";
			plpString += "\t\tProbabilidade: " + oneParsedPhrase.probability + "\n";
			plpString += "\t\tFoi necessário alisamento (Laplace)?: ";
			
			if (oneParsedPhrase.unsmoothed == true){
				plpString += "Não";
			}else{
				plpString += "Sim";
			}
			
			plpString += "\n\n";
			plpString += "\t\t\t\tProbabilidades dos lemas analisados";
			
			for (PhraseData onePhraseData : oneParsedPhrase.phraseDatas) {
			
				plpString += "\n\n";
				
				plpString += "\t\t\t\tLema: " + onePhraseData.lema + "\n";
				plpString += "\t\t\t\tFrase com o lema: " + onePhraseData.phrasewithlema + "\n";
				plpString += "\t\t\t\tProbabilidade: " + onePhraseData.probability + "\n";
				
			}		
			
			plpString += "\n------------------------------------------------------------\n\n";
			
			i++;
			
		}
		
		return plpString;
				
	}
	
	/*
	 * calculates the probability of the bigram received as input
	 */
	private double calcUnigramProbability(String unigram, boolean smoothed, int nTokens, int vTokens){
		
		double unigramProbability = 0;
		
		if (nGramExists(unigram, 1)){
			
			if(smoothed == false){
				unigramProbability = (double)(hmunigram.get(unigram)) / (double)(nTokens);
			}else{
				unigramProbability = (double)(hmunigram.get(unigram) + 1) / (double)(nTokens + vTokens);
			}			
			
		}
		
		return unigramProbability;
		
	}
	
	/*
	 * calculates the reconstructed probability when Laplace smoothing is used
	 */
	private double reconstructedProbability(String bigram, int nvTokens){
		
		double reconstructedProbability = 0;
		
		String[] splitedbigram = bigram.split(" ");
		
		if(splitedbigram.length == 2 && nGramExists(bigram, 2) && nGramExists(splitedbigram[0], 1)){
			
			reconstructedProbability = ((double)(hmbigram.get(bigram) + 1) * (double)(hmunigram.get(splitedbigram[0]))) / (double)(hmunigram.get(splitedbigram[0]) + nvTokens);
			
		}
		
		return reconstructedProbability;
		
	}
	
	/*
	 * class to store data for each sentence after the probability is calculated
	 */
	public class PhraseData {
		public String lema;
		public String phrasewithlema;
		public boolean smoothed;
		public Double probability;

		public PhraseData(){
		}
		
		public PhraseData(String lema, String phrasewithlema){
			this.lema = lema;
			this.phrasewithlema = phrasewithlema;
		}
	}
	
	/*
	 * class to store data about the most probable lema for each processed sentence and the data for all the options considered
	 */
	public class ParsedPhrase {
		public String mostProbLema;
		public Double probability;
		public boolean unsmoothed;
		List<PhraseData> phraseDatas = new ArrayList<PhraseData>();
		
	}
	
}
