package screen;

// import AWT
import java.awt.Dimension;
import java.awt.Rectangle;

// Import custom Panel and SortingAlgorithm function
import customJComponent.CustomJPanel;
import sortingAlgorithm.SortingAlgorithm;

// Declare MainScreen class
public class MainScreen extends CustomJPanel {
	private static final long serialVersionUID = 1L; // Set Serial Version UID for serialization compatibility
	
	// Declare instance variables
	private SortingAlgorithm selected_algorithm = SortingAlgorithm.NO_ALGORITHM;
	private Menu menuScreen;
	private SortingPanel sortingScreen;
	private SidePanel sidePanel;
	
	// Declare PROCESS enum to represent various actions
	public static enum PROCESS {
		SORT, PAUSE, RESUME, SHUFFLE, RESET, SIDEPANEL_ANIMATION;
	}
	
	// Constructor for MainScreen
	public MainScreen(Dimension dim) {
		super(dim);
		createAndDisplayGUI();
	}

	// Method to create and display the GUI components
	private void createAndDisplayGUI() {
		// Initialize menuScreen with a Rectangle defining its position and size
		menuScreen = new Menu(this, new Rectangle(0, 0, 
									  			this.getBounds().width, 
									  			this.getBounds().height / 10));
		
		// Initialize sortingScreen below the menuScreen
		sortingScreen = new SortingPanel(this, new Rectangle(0, 
												menuScreen.getY() + menuScreen.getHeight(), 
												this.getBounds().width, 
												this.getBounds().height - menuScreen.getHeight()));
		
		// Initialize sidePanel, initially off-screen to the left
		sidePanel = new SidePanel(this, new Rectangle(-1 * this.getBounds().width / 4, 
												sortingScreen.getY(), 
												this.getBounds().width / 4, 
												sortingScreen.getHeight()));
		
		// Add the components to the MainScreen
		add(menuScreen);
		add(sidePanel);
		add(sortingScreen);
	}
	
	// Method to start a specific process
	public void startProcess(PROCESS process) {
		switch(process) {
		case SORT:
			sortingScreen.sort();
			break;
		case PAUSE:
			sortingScreen.pause();
			break;
		case RESUME:
			sortingScreen.resume();
			break;
		case SHUFFLE:
			sortingScreen.randomizeBarHeight();
			sortingScreen.setAlgorithm(selected_algorithm);
			break;
		case RESET:
			sortingScreen.resetScreen();
			break;
		case SIDEPANEL_ANIMATION:
			sidePanel.startSidePanelAnimation();
			break;
		default:
			System.out.println("Process Start Request Error: " + 
								process + " Undefined Process");
			break;
		}
	}
	
	// Method to handle completion of a process
	public void doneProcess(PROCESS process) {
		switch(process) {
		case SORT:
			menuScreen.doneSorting();
			break;
		case SHUFFLE:
			menuScreen.doneShuffling();
			break;
		default:
			System.out.println("Process Done Flag Error: " + 
								process + " Undefined Process");
			break;
		}
	}

	// Method to change a specific value
	public void changeValue(String name, int value) {
		switch(name) {
		case "SIZE":
			sortingScreen.setBarSize(value);
			break;
		case "SPEED":
			sortingScreen.setSpeed((long)value);
			break;
		default:
			System.out.println("Change Value Error: " + 
								name + " Undefined variable");
			break;
		}
	}
	
	// Method to set the sorting algorithm
	public void setAlgorithm(SortingAlgorithm algorithm) {
		this.selected_algorithm = algorithm;
		sortingScreen.setAlgorithm(algorithm);
		menuScreen.donePicking();
	}
	
	// Method to check if the side panel is shown
	public boolean isSidePanelShown() {
		return sidePanel.isShown();
	}
	
	// Method to check if an algorithm has been chosen
	public boolean hasChosenAnAlgorithm() {
		return selected_algorithm != SortingAlgorithm.NO_ALGORITHM;
	}
}
