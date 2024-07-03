package screen;


// Import AWT Package
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

// Import Swing Package
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.SwingWorker;

// Import Custom Component and SortingAlgorithm 
import customJComponent.CustomJButton;
import customJComponent.CustomJLabel;
import customJComponent.CustomJPanel;
import sortingAlgorithm.SortingAlgorithm;

// Inisialize Class Side Panel, extends CustomJPanel
public class SidePanel extends CustomJPanel{
	private static final long serialVersionUID = 1L;
	
	private MainScreen main_screen;
	private SwingWorker<Void, Void> side_panel_animation1;
	private boolean is_showing_in = false;
	
	public SidePanel(MainScreen mainScreen, Rectangle dim) {
		super(dim); // Call the superclass constructor with the given dimensions
		setBackground(Color.DARK_GRAY); // Set the background color of the panel to dark gray
		setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Set the border of the panel with a white line of thickness 2
	
		main_screen = mainScreen; // Initialize the main screen reference
		initComponent(dim.width, dim.height); // Initialize the components with the given width and height
	}
	
	private void initComponent(int width, int height) {
		final int fixed_x = 0, title_height = height / 5; // Define fixed x position and calculate title height as one-fifth of the panel height
		int y = 0, font_size = 30; // Initialize y position and font size
	
		String title_text = "<html><center>SORTING<br />ALGORITHM</center></html>"; // Define the title text with HTML formatting
		add(new CustomJLabel(new Rectangle(fixed_x, y, width, title_height), 
							title_text, new Font("Arial", Font.PLAIN, font_size))); // Add the title label to the panel
	
		font_size = 20; // Update font size for the selected algorithm label
		y = title_height; // Update y position after the title label
		int selected_algo_height = title_height; // Set the height of the selected algorithm label equal to the title height
		String selected_algorithm_text = "NONE"; // Initialize the selected algorithm text
		CustomJLabel selected_algorithm = new CustomJLabel(new Rectangle(fixed_x, y, width, selected_algo_height), 
															selected_algorithm_text, 
															new Font("Arial", Font.PLAIN, font_size)) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void setText(String text) {
				String selected_algorithm = "<html><center>SELECTED SORTING : <br />" + text + "</center></html>"; // Format the text with HTML
				super.setText(selected_algorithm); // Call the superclass setText method with the formatted text
			}
		};
		add(selected_algorithm); // Add the selected algorithm label to the panel
	
		AbstractAction action = new AbstractAction() { // Define an action for button clicks
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = ((CustomJButton)e.getSource()).getText(); // Get the text of the clicked button
				selected_algorithm.setText(text); // Update the selected algorithm label with the button text
				
				main_screen.setAlgorithm(SortingAlgorithm.getAlgorithmViaText(text)); // Set the algorithm in the main screen based on the button text
			}
		};
	
		/*
		 *  divide the remaining spaces depending on the number of sorting
		 *  algorithms minus the "NO_ALGORITHM" 
		 */
		int button_height = (height - (title_height + selected_algo_height)) 
							/ (SortingAlgorithm.values().length - 1); // Calculate button height by dividing remaining height by the number of algorithms
		y = selected_algorithm.getY() + selected_algorithm.getHeight(); // Update y position after the selected algorithm label
	
		for(SortingAlgorithm algo : SortingAlgorithm.values()) { // Iterate through all sorting algorithms
			if("NO_ALGORITHM".equals(algo.toString())) continue; // Skip the "NO_ALGORITHM" entry
			
			add(new CustomJButton(new Rectangle(fixed_x, y, width, button_height), 
					action, 
					algo.toString().replace("_", " "))); // Add a button for each sorting algorithm with its name as the text
			y += button_height; // Update y position for the next button
		}
	}
	
	public void startSidePanelAnimation() {
		is_showing_in = !is_showing_in; // Toggle the visibility state of the side panel
	
		// Stop execution if it is currently running, then execute again, this time in opposite direction
		if (side_panel_animation1 != null && !side_panel_animation1.isDone())
			side_panel_animation1.cancel(true); // Cancel the current animation if it is running
	
		side_panel_animation1 = new SwingWorker<>() { // Create a new SwingWorker to handle the animation
			@Override
			protected Void doInBackground() throws InterruptedException {
				while (!isCancelled()) { // Continue the loop until the task is cancelled
					if (is_showing_in && getX() <= 0) // If the panel is showing in and not fully visible yet
						setLocation(getX() + 1, getY()); // Move the panel to the right by 1 pixel
					else if (!is_showing_in && getX() >= -1 * CUSTOM_JPANEL_WIDTH) // If the panel is hiding and not fully hidden yet
						setLocation(getX() - 1, getY()); // Move the panel to the left by 1 pixel
					else
						break; // Break the loop if the panel has reached its target position
				}
				return null;
			}
	
			@Override
			protected void done() {
				super.done(); // Call the superclass done method
				side_panel_animation1.cancel(true); // Ensure the animation task is cancelled
			}
		};
	
		side_panel_animation1.execute(); // Start the animation task
	}
	
	public boolean isShown() {
		return getX() > -getWidth(); // Return true if the panel is visible, false otherwise
	}
	
}