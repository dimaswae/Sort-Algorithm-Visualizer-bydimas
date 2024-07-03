// Run code from here (Main.java)
package main;

// Importing package
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

// Import MainScreen
import screen.MainScreen;

public class Main { 
    // Set final width dan height frame
    public static final int SCREEN_WIDTH = 1080;
    public static final int SCREEN_HEIGHT = SCREEN_WIDTH * 9/16 ;

    private JFrame frame = null;

    // Start Configuration
    private void start() {
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

        MainScreen screen = new MainScreen(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); // Deklarasi screen and Add width heigth to MainScreen

        // Create the frame
        frame = new JFrame("Sorting Algorithm Visualizer by-Dimas"); // Project title 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Add close/Exit button to frame
        frame.setResizable(false); // Make frame unresizable
        frame.add(screen); // Add MainScreen to the frame
        frame.validate();
        frame.pack();
        frame.setLocationRelativeTo(null); 
        screen.requestFocusInWindow();

        // Display the window
        frame.setVisible(true);
    }

    // Run Code from here
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main().start();
			}
		});
    }
}
