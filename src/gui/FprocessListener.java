package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import business.PhraseLemaParser;

/**
 * 
 * @author Grupo 02 - João Gonçalves - número 68041 
 *
 */

public class FprocessListener implements ActionListener{

	final JFileChooser fcprocess = new JFileChooser();
	public static Scanner sunigram = null;
	public static Scanner sbigram = null;
	public static Scanner sparametrization = null;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		String command = ((JButton) e.getSource()).getName();
		
		if (e.getSource() instanceof JButton) {

				int returnVal;
			
				if (command.equals("unigram")){
					
					returnVal = fcprocess.showOpenDialog(WindowSelector.getFrame());
					
					if (returnVal == JFileChooser.APPROVE_OPTION){
						try {
							sunigram = new Scanner((File)fcprocess.getSelectedFile());
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}else if (command.equals("bigram")){

					returnVal = fcprocess.showOpenDialog(WindowSelector.getFrame());
					
					if (returnVal == JFileChooser.APPROVE_OPTION){
						try {
							sbigram = new Scanner((File)fcprocess.getSelectedFile());
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}else if (command.equals("parametrization")){
					
					returnVal = fcprocess.showOpenDialog(WindowSelector.getFrame());
					
					if (returnVal == JFileChooser.APPROVE_OPTION){
						try {
							sparametrization = new Scanner((File)fcprocess.getSelectedFile());
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}else if (command.equals("go")){
				
					if (sunigram != null && sbigram != null && sparametrization != null && !WindowSelector.getTaphrase().getText().equals("")){
						PhraseLemaParser parsePhrases = new PhraseLemaParser(WindowSelector.getTaphrase().getText().split("\\n"), sunigram, sbigram, sparametrization);
						
						Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
						
						int dimwidth = dim.width/2;
						int dimheight = dim.height/2;
						
						JFrame ppFrame = new JFrame("Processed phrases probabilities");
						ppFrame.setSize(dimwidth, dimheight);
												
						JPanel mainpanel = new JPanel(new BorderLayout());
						ppFrame.add(mainpanel);
												
						JTextArea taResult = new JTextArea();
						taResult.setText(parsePhrases.sresult);
						mainpanel.add(taResult);
						
						JScrollPane jsp = new JScrollPane(mainpanel);
						ppFrame.add(jsp);
						
						ppFrame.setVisible(true);
						
					}else{
						JOptionPane.showMessageDialog(WindowSelector.getFrame(), "Please insert test sentences and select all the needed files");
					}
					
				}
				
			
					
		}
		
	}
	
}
