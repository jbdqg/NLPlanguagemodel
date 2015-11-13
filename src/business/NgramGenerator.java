package business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * @author Grupo 02 - João Gonçalves - número 68041 
 *
 */

public class NgramGenerator {

	static public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
	
	private static Scanner isfinalFile;
	private static String finalFilePath;
	public static List<String> filePaths = new ArrayList<String>();
		
	public NgramGenerator(String receivedFinalFilePath) throws FileNotFoundException{
		
		/**
		 * generated the unigram and bigram files with and witouth Laplace smoothing
		 * the generated files are equivalent to the command of srilm:
		 * ./ngram-count -text /<work dir>/foiIrSer-2.final -sort -write2 /<work dir>/corpus.count
		 */		
		setFinalFile(new Scanner(new File(receivedFinalFilePath)));
		isfinalFile = getFinalFile();
		setFinalFilePath(receivedFinalFilePath);
		new Ngram(this, 1, false);
		isfinalFile.close();
		
		setFinalFile(new Scanner(new File(receivedFinalFilePath)));
		isfinalFile = getFinalFile();
		setFinalFilePath(receivedFinalFilePath);
		new Ngram(this, 1, true);
		isfinalFile.close();
		
		setFinalFile(new Scanner(new File(receivedFinalFilePath)));
		isfinalFile = getFinalFile();
		setFinalFilePath(receivedFinalFilePath);
		new Ngram(this, 2, false);
		isfinalFile.close();
		
		setFinalFile(new Scanner(new File(receivedFinalFilePath)));
		isfinalFile = getFinalFile();
		setFinalFilePath(receivedFinalFilePath);
		new Ngram(this, 2, true);
		isfinalFile.close();

	}

	static public Scanner getFinalFile() {
		return isfinalFile;
	}


	static public void setFinalFile(Scanner finalFile) {
		NgramGenerator.isfinalFile = finalFile;
	}
	
	public static String getFinalFilePath() {
		return finalFilePath;
	}

	public static void setFinalFilePath(String finalFilePath) {
		NgramGenerator.finalFilePath = finalFilePath;
	}

	public class Ngram {
		
		private int nGramDimension;
		private boolean smoothed;
		private TreeMap<String, Integer> hashGram = null;
		
		public Ngram(NgramGenerator gramFileToProcess, int nGramDimension, boolean smoothed){
			
			setnGramDimension(nGramDimension);
			this.nGramDimension = getnGramDimension();
			
			setHashGram(new TreeMap<String, Integer>());			
			this.hashGram = getHashGram();
			
			if (smoothed == true){
				this.smoothed = true;
			}else{
				this.smoothed = false;
			}
			
			processGramCorpusFile();
			
		}
				
		public int getnGramDimension() {
			return nGramDimension;
		}
		
		public void setnGramDimension(int nGramDimension) {
			this.nGramDimension = nGramDimension;
		}
		
		public TreeMap<String, Integer> getHashGram() {
			return hashGram;
		}
		public void setHashGram(TreeMap<String, Integer> hashGram) {
			this.hashGram = hashGram;
		}
		
		public boolean isSmoothed() {
			return smoothed;
		}

		public void setSmoothed(boolean smoothed) {
			this.smoothed = smoothed;
		}
		
		/*
		 * generates each ngram file, based on the corpus file		
		 */
		public PrintWriter processGramCorpusFile(){
			
			PrintWriter gramData = null;			
			
			while(NgramGenerator.getFinalFile().hasNextLine()){
				
				String lineWords[] = NgramGenerator.getFinalFile().nextLine().split(" ");

				List<String> lineWordsList = new ArrayList<String>();
				
				lineWordsList.add("<s>");
				for (int i = 0; i < lineWords.length; i++){
					
					lineWordsList.add(lineWords[i]);
					
				}
				lineWordsList.add("</s>");
				
				for (int i = 0; i < lineWordsList.size() - (this.nGramDimension - 1); i++){
					
					String oneNgram = new String("");
					
					for(int j = 0; j < nGramDimension; j++){
						oneNgram += lineWordsList.get(i+j) + " ";
					}
					
					oneNgram = oneNgram.trim();
					
					if (this.hashGram.containsKey(oneNgram) == true){
						this.hashGram.put(oneNgram, hashGram.get(oneNgram) + 1);
					}else{
						if (isSmoothed()){
							this.hashGram.put(oneNgram, 2);
						}else{
							this.hashGram.put(oneNgram, 1);
						}
						
					}
											
				}
				
			}
				
			NgramGenerator.getFinalFile().close();
				
			/*
			 * creates the ngram file with and without Laplace smoothing		
			 */
			if (!this.getHashGram().isEmpty()){
				
				try {
					
					String ngramName = new String("");
					
					if (this.nGramDimension == 1){
						ngramName = "unigram";
					}else if (this.nGramDimension == 2){
						ngramName = "bigram";
					}else{
						ngramName = "grams";
					}
					
					if (isSmoothed()){
						
						gramData = new PrintWriter(new File(NgramGenerator.getFinalFilePath() + ngramName + "_smoothed"));
						filePaths.add(NgramGenerator.getFinalFilePath() + ngramName + "smoothed");
					}else{
						gramData = new PrintWriter(new File(NgramGenerator.getFinalFilePath() + ngramName));
						filePaths.add(NgramGenerator.getFinalFilePath() + ngramName);						
					}
					
					Set<String> keySet = this.getHashGram().keySet();
					Iterator<String> keySetIterator = keySet.iterator();
					
					while (keySetIterator.hasNext()){
						String key = keySetIterator.next();
						gramData.print(key + "\t" + this.getHashGram().get(key) + "\n");

					}
					
					gramData.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				gramData.close();
				
			}
			
			return gramData;
			
		}

	}
	
	
	
	
}
