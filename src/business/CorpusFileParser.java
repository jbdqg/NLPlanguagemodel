package business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * 
 * @author Grupo 02 - João Gonçalves - número 68041 
 *
 */

public class CorpusFileParser {
		
	private static Scanner ofile;
	private static PrintWriter pfinalfile;
	public static String pfinalfilepath;

	public CorpusFileParser(String ambiguousWord, Scanner sFile, String corpusFilePath) throws IOException{
		
		CorpusFileParser.setOfile(sFile);
		setPfinalfile(corpusFilePath);
		try {
			parseFile(ambiguousWord);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Scanner getOfile() {
		return ofile;
	}

	public static void setOfile(Scanner ofile) {
		CorpusFileParser.ofile = ofile;
	}
	
	/*
	 * method that parses the .out file and replace the ambiguous word with the correspondent lema 
	 */
	private void parseFile(String ambiguousWord) throws FileNotFoundException, IOException{
		
		if(ofile != null){
			
			while(ofile.hasNext()){
				
				String[] oneLineContent = ofile.nextLine().split("\t");
				
				if(oneLineContent.length == 2){
					
					if(oneLineContent[0].equals(new String(""))){
						
						//TODO

					}else if(!oneLineContent[0].contains("?") && !oneLineContent[0].contains("#")){
					
						getPfinalfile().println(oneLineContent[1].replaceAll(getWordRegex(ambiguousWord), " " + oneLineContent[0].toUpperCase() + " "));
						
					}else{
						continue;
					}
					
				}
				
			}
				
		}
		
	}
	
	/*
	 * method that creates the regex expression for the ambiguous word to replace on each sentence
	 */
	private String getWordRegex(String ambiguousWord) {
		
		String regexanychar = new String("[^a-z,.,;,\"]*");
		
		String regcorpus = regexanychar;
		
		for (int i = 0; i < ambiguousWord.length(); i++){
			regcorpus += "[" + (String)(ambiguousWord.charAt(i)+"").toLowerCase() + "|" + (String)(ambiguousWord.charAt(i)+"").toUpperCase() + "]";
		}
		
		regcorpus += regexanychar;
		
		return regcorpus;
		
	}
	
	public static PrintWriter getPfinalfile() {
		return pfinalfile;
	}

	public static void setPfinalfile(String corpusFilePath) throws FileNotFoundException {
		
		CorpusFileParser.pfinalfilepath = new String(corpusFilePath.replace((String)"out", (String)"final"));
		
		CorpusFileParser.pfinalfile = new PrintWriter(new File(CorpusFileParser.pfinalfilepath));
		
	}
	
}
