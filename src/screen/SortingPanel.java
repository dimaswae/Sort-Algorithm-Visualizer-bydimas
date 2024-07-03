package screen;

// Import AWT Package
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

// Import swing worker Package
import javax.swing.SwingWorker;

// Import Custom Component
import customJComponent.CustomJPanel;
import customJComponent.CustomSwingWorker;
import sortingAlgorithm.SortingAlgorithm;

// Inisialize Class SortingPanel, extends CustomJPanel
public class SortingPanel extends CustomJPanel {
	private static final long serialVersionUID = 1L; // Ensure the class can be serialized and deserialized correctly

	private MainScreen mainScreen; // Reference to the main screen

	private final float BAR_STROKE_THICKNESS = 2f; // Thickness of the bar stroke
	private final int FONT_SIZE = 15; // Font size for algorithm text
	private final Font ALGORITHM_FONT = new Font("Arial", Font.PLAIN, FONT_SIZE); // Font used for algorithm text

	/* default values */
	private int size = Menu.INIT_SIZE_SLIDER_VAL; // Initial size value from menu
	private long speed = Menu.INIT_SPEED_SLIDER_VAL; // Initial speed value from menu

	private SortingAlgorithm current_algorithm = SortingAlgorithm.NO_ALGORITHM; // Currently selected sorting algorithm
	private double bar_width; // Width of each bar
	private double bar_height[]; // Heights of the bars

	private CustomSwingWorker<Void, Void> sortingWorker; // Worker thread for sorting

	// -1 means the variable is not yet initialized
	private int current_bar_index = -1; // Index of the current bar being processed
	private int traversing_bar_index = -1; // Index of the bar being traversed
	private int selected_bar_index = -1; // Index of the selected bar

	public SortingPanel(MainScreen mainScreen, Rectangle rect) {
		super(rect); // Call the superclass constructor with the given rectangle
		this.mainScreen = mainScreen; // Initialize the main screen reference

		initBarsDimension(); // Initialize the dimensions of the bars
	}

	private void initBarsDimension() {
		bar_width = (double) CUSTOM_JPANEL_WIDTH / size; // Calculate the width of each bar based on the panel width and
															// size
		bar_height = new double[size]; // Initialize the bar height array with the given size

		double height_interval = (double) CUSTOM_JPANEL_HEIGHT / size; // Calculate the height interval for each bar

		for (int i = 0; i < size; i++)
			bar_height[i] = height_interval * (i + 1.0) - BAR_STROKE_THICKNESS; // Set the height of each bar

		repaint(); // Repaint the panel to reflect the changes
	}

	public void randomizeBarHeight() {
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws InterruptedException {
				final long random_tick_speed = 10; // Speed of the randomization tick
				int left = 0, right = size - 1; // Initialize left and right pointers

				for (; left < size / 2; left++, right--) { // Loop from both ends to the center
					int rand_index;
					double temp;

					// Randomize left -> center
					rand_index = (int) (Math.random() * size); // Generate random index
					temp = bar_height[left]; // Swap heights
					bar_height[left] = bar_height[rand_index];
					bar_height[rand_index] = temp;

					// Randomize right -> center
					rand_index = (int) (Math.random() * size); // Generate random index
					temp = bar_height[right]; // Swap heights
					bar_height[right] = bar_height[rand_index];
					bar_height[rand_index] = temp;

					repaint(); // Repaint the panel to reflect changes
					Thread.sleep(random_tick_speed); // Pause for a short duration
				}

				return null;
			}

			@Override
			protected void done() {
				super.done(); // Call the superclass method
				mainScreen.doneProcess(MainScreen.PROCESS.SHUFFLE); // Notify the main screen that shuffling is done
			}
		};
		worker.execute(); // Execute the worker thread
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Call the superclass method
		super.paintComponents(g); // Paint CustomJComponents
		Graphics2D g2d = (Graphics2D) g; // Cast Graphics to Graphics2D

		Stroke old = g2d.getStroke(); // Save the current stroke
		g2d.setStroke(new BasicStroke(BAR_STROKE_THICKNESS)); // Set the stroke thickness

		g2d.setColor(Color.CYAN); // Set the color for bars

		double x, y;
		for (int i = 0; i < size; i++) { // Loop through all bars
			x = i * bar_width; // Calculate x position
			y = CUSTOM_JPANEL_HEIGHT - (BAR_STROKE_THICKNESS + bar_height[i]); // Calculate y position

			Rectangle2D.Double rect = new Rectangle2D.Double(x, y, bar_width, bar_height[i]); // Create a rectangle for
																								// the bar
			g2d.fill(rect); // Fill the rectangle
		}

		if (current_algorithm != SortingAlgorithm.NO_ALGORITHM) // If an algorithm is selected
			drawAlgorithm(g2d); // Draw the algorithm-related components

		g2d.setStroke(old); // Restore the original stroke
		g2d.dispose(); // Dispose of the graphics context
	}

	private void drawAlgorithm(Graphics2D g2d) {
		double x, y;

		if ((current_bar_index = current_algorithm.get_current_index()) != -1) { // If current bar index is set
			g2d.setColor(Color.GREEN); // Set color to green

			x = current_bar_index * bar_width; // Calculate x position
			y = CUSTOM_JPANEL_HEIGHT - (BAR_STROKE_THICKNESS + bar_height[current_bar_index]); // Calculate y position

			Rectangle2D.Double curr_rect = new Rectangle2D.Double(x, y, bar_width, bar_height[current_bar_index]); // Create
																													// rectangle
			g2d.fill(curr_rect); // Fill the rectangle
		}

		if ((traversing_bar_index = current_algorithm.get_traversing_index()) != -1) { // If traversing bar index is set
			g2d.setColor(Color.RED); // Set color to red

			x = traversing_bar_index * bar_width; // Calculate x position
			y = CUSTOM_JPANEL_HEIGHT - (BAR_STROKE_THICKNESS + bar_height[traversing_bar_index]); // Calculate y
																									// position

			Rectangle2D.Double select_rect = new Rectangle2D.Double(x, y, bar_width, bar_height[traversing_bar_index]); // Create
																														// rectangle
			g2d.fill(select_rect); // Fill the rectangle
		}

		if ((selected_bar_index = current_algorithm.get_selected_index()) != -1) { // If selected bar index is set
			g2d.setColor(Color.MAGENTA); // Set color to magenta

			x = selected_bar_index * bar_width; // Calculate x position
			y = CUSTOM_JPANEL_HEIGHT - (BAR_STROKE_THICKNESS + bar_height[selected_bar_index]); // Calculate y position

			Rectangle2D.Double select_rect = new Rectangle2D.Double(x, y, bar_width, bar_height[selected_bar_index]); // Create
																														// rectangle
			g2d.fill(select_rect); // Fill the rectangle
		}

		// Information display
		g2d.setColor(Color.WHITE); // Set color to white
		g2d.setFont(ALGORITHM_FONT); // Set font

		int string_y_padding = 20, // Padding for y position of text
				string_x_padding = 200; // Padding for x position of text

		// Current algorithm
		x = 20; // Initial x position
		y = 20; // Initial y position
		g2d.drawString("Current Algorithm : ", (int) x, (int) y); // Draw the text

		x += string_x_padding; // Adjust x position
		String algorithm = current_algorithm.toString().replace("_", " "); // Format algorithm name
		g2d.drawString(algorithm, (int) x, (int) y); // Draw the algorithm name

		// Array access
		x -= string_x_padding; // Reset x position
		y += string_y_padding; // Adjust y position
		g2d.drawString("Array Access : ", (int) x, (int) y); // Draw the text

		x += string_x_padding; // Adjust x position
		String array_access = String.valueOf(current_algorithm.get_array_access()); // Get array access count
		g2d.drawString(array_access, (int) x, (int) y); // Draw the array access count
	}

	public void sort() {
		// Initialize a new CustomSwingWorker to perform sorting
		sortingWorker = new CustomSwingWorker<>() {
			@Override
			protected Void doInBackground() {
				// Perform the sorting algorithm on the bar heights with the specified speed
				current_algorithm.performAlgorithm(bar_height, speed);
				return null;
			}

			@Override
			protected void done() {
				// If the task was cancelled, return early
				if (isCancelled())
					return;
				// Call the superclass done method
				super.done();
				// Notify the main screen that the sorting process is done
				mainScreen.doneProcess(MainScreen.PROCESS.SORT);
			}
		};
		// Execute the sorting worker
		sortingWorker.execute();
	}

	public void pause() {
		// Print a message indicating the process is paused
		System.out.println("Process is paused...");
		// Pause the current algorithm
		current_algorithm.pause();
	}

	public synchronized void resume() {
		// Print a message indicating the process is continuing
		System.out.println("Continue...");
		// Resume the current algorithm
		current_algorithm.resume();
	}

	public void setBarSize(int size) {
		// Set the size of the bars
		this.size = size;
		// Initialize the dimensions of the bars
		initBarsDimension();
	}

	public void setSpeed(long speed) {
		// Set the speed of the sorting
		this.speed = speed;
		// If an algorithm is selected, update its speed
		if (current_algorithm != SortingAlgorithm.NO_ALGORITHM)
			current_algorithm.set_speed(speed);
	}

	public void setAlgorithm(SortingAlgorithm algorithm) {
		// Set the current algorithm
		this.current_algorithm = algorithm;
		// If a valid algorithm is selected, set the sorting panel for the algorithm
		if (algorithm != SortingAlgorithm.NO_ALGORITHM)
			current_algorithm.set_sorting_panel(this);
		// Reset the screen to its initial state
		resetScreen();
	}

	public synchronized void resetScreen() {
		// If a valid algorithm is selected, reset the algorithm
		if (current_algorithm != SortingAlgorithm.NO_ALGORITHM)
			current_algorithm.reset();
		// Reset the current bar index
		current_bar_index = -1;
		// Reset the traversing bar index
		traversing_bar_index = -1;
		// Reset the selected bar index
		selected_bar_index = -1;

		// If there is an active sorting worker, cancel it
		if (sortingWorker != null) {
			sortingWorker.cancel(true);
		}
	}

}