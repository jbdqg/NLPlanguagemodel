package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * @author Grupo 02 - João Gonçalves - número 68041 
 *
 */

public class WindowSelector {

	private static JFrame frame;
	private static JButton bopen;
		
	private static JTextField tfcorpus;
	private static JTextArea taphrase;
	private JPanel mainpanel;
	
	private JPanel fileSelectPanel;
	private JPanel processPhrasePanel;
	
	public WindowSelector(){
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int dimwidth = dim.width/2;
		int dimheight = dim.height/2;
		
		frame = new JFrame("Probabilistic Language Model");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(dimwidth, dimheight);
		
		frame.setLocation(dimwidth-frame.getSize().width, dimheight-frame.getSize().height);
		
		mainpanel = new JPanel(new GridLayout());
		frame.add(mainpanel);
		
		drawCorpusInputPanel(dimheight);
		
		mainpanel.add(fileSelectPanel);
		
		drawPhraseInputPanel(dimwidth, dimheight);
		
		mainpanel.add(processPhrasePanel);
		
	}
	
	
	private void drawCorpusInputPanel(int dimheight){
		
		fileSelectPanel = new JPanel(new FlowLayout());
		
		JLabel lfile = new JLabel("insert the ambiguous word: ");
		fileSelectPanel.add(lfile);
		setTfcorpus(new JTextField());
		getTfcorpus().setPreferredSize(new Dimension(dimheight/6,20));
		fileSelectPanel.add(getTfcorpus());
		setBopen(new JButton("select corpus file (*.out)"));
		bopen.addActionListener(new FopenListener());
		fileSelectPanel.add(getBopen());
		
	}
	
	private void drawPhraseInputPanel (int dimwidth, int dimheight){
		
		processPhrasePanel = new JPanel(new FlowLayout());
				
		FprocessListener processlistener = new FprocessListener();
		
		JLabel labelPhrase = new JLabel("insert sentences to test (with tokenization):");
		processPhrasePanel.add(labelPhrase);
		setTaphrase(new JTextArea());
		getTaphrase().setPreferredSize(new Dimension(dimwidth/3, dimheight/2));
		processPhrasePanel.add(getTaphrase());
		JButton bprocess = new JButton("process sentences probabilities");
		bprocess.setName("go");
		bprocess.addActionListener(processlistener);
		
		JButton bunigram = new JButton("select unigram file (not smoothed)");
		bunigram.setName("unigram");
		bunigram.addActionListener(processlistener);
		processPhrasePanel.add(bunigram);
		
		JButton bbigram = new JButton("select bigram file (not smoothed)");
		bbigram.setName("bigram");
		bbigram.addActionListener(processlistener);
		processPhrasePanel.add(bbigram);
		
		JButton bparametrization = new JButton("select parametrization file");
		bparametrization.setName("parametrization");
		bparametrization.addActionListener(processlistener);
		processPhrasePanel.add(bparametrization);
		
		processPhrasePanel.add(bprocess);
		
	}
	
	
	
	public void init(){
		getFrame().setVisible(true);
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
	public static JButton getBopen(){
		return bopen;
	}
	
	public static void setBopen(JButton bopen) {
		WindowSelector.bopen = bopen;
	}

	public static JTextField getTfcorpus() {
		return tfcorpus;
	}

	public static void setTfcorpus(JTextField tfcorpus) {
		WindowSelector.tfcorpus = tfcorpus;
	}


	public static JTextArea getTaphrase() {
		return taphrase;
	}


	public static void setTaphrase(JTextArea taphrase) {
		WindowSelector.taphrase = taphrase;
	}

	
	
}
