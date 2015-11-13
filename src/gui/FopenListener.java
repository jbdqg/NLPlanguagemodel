package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import business.CorpusFileParser;
import business.NgramGenerator;

/**
 * 
 * @author Grupo 02 - João Gonçalves - número 68041 
 *
 */

public class FopenListener implements ActionListener{

	final JFileChooser fc = new JFileChooser();
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if (e.getSource() == WindowSelector.getBopen()) {
			System.out.println("button");
			
			int returnVal = fc.showOpenDialog(WindowSelector.getFrame());

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	
	            //TODO colocar na frame a opção de ao selecionar o file, fazer o parse e criar o ficheiro final
	        	
	        	Scanner sSelectedFile = null;
	        	
	        	try {
					
	        		/**
					 *  faz o parse do file .out e devolve o final
					 */
	        		
	        		
	        		if (WindowSelector.getTfcorpus().getText().isEmpty()){
	        			
	        			JOptionPane.showMessageDialog(WindowSelector.getFrame(), "Please insert the ambiguous word before the corpus file selection");
	        			
	        		}else{
	        			
	        			sSelectedFile = new Scanner((File)fc.getSelectedFile());
		        		
		        		String sResult = new String("The following files were generated: \n");
		        		
		        		new CorpusFileParser(WindowSelector.getTfcorpus().getText(), sSelectedFile, fc.getSelectedFile().getAbsolutePath());
		        		
		        		//apanhar do input o gram a gerar (unigram ou bigram ou ngram)
		        		NgramGenerator ngramGenerated = new NgramGenerator(CorpusFileParser.pfinalfilepath);

		        		if (CorpusFileParser.pfinalfilepath != null){
		        			
		        			sResult += "\t .final final: \n";
		        			sResult += "\t\t\t\t" + CorpusFileParser.pfinalfilepath + "\n\n";
		        			
		        		}
		        		
		        		Iterator<String> filePathsIterator = ngramGenerated.filePaths.iterator();
		        		if (filePathsIterator.hasNext()){
		        			
		        			sResult += "\t ngram files: \n";
		        			
		        			while (filePathsIterator.hasNext()) {
		        				sResult += "\t\t\t\t" + (filePathsIterator.next()) + "\n";
			        		}
		        			
		        		}
		        		
		        		JOptionPane.showMessageDialog(WindowSelector.getFrame(), sResult);
		        		
	        		}
	        		
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally{
					if (sSelectedFile != null){
						sSelectedFile.close();
					}
				}
	            	
	            //This is where a real application would open the file.
	            System.out.println("Opening: " + fc.getSelectedFile().getAbsolutePath() + ".");
	        } else {
	        	System.out.println("Open command cancelled by user.");
	        }
		}
		
	}
	
}
