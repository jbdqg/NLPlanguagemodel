import gui.WindowSelector;

/**
 * 
 * @author Grupo 02 - João Gonçalves - número 68041 
 *
 */

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	WindowSelector window = new WindowSelector();
        		window.init();
            }
		 });
		
	}

}
