package screen;

// Importing AWT package
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

// Importing Swing package
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// Importing Custom Component
import customJComponent.CustomJButton;
import customJComponent.CustomJLabel;
import customJComponent.CustomJPanel;
import customJComponent.CustomJSlider;

// Inisialize Class Menu, extends from CustomJPanel 
public class Menu extends CustomJPanel {
    private static final long serialVersionUID = 1L;
    private MainScreen mainScreen;
    private CustomJButton burger, sort, stop, shuffle;
    private CustomJLabel title, sliderName, sliderValue;
    private CustomJSlider sizeSlider, speedSlider;

    private Timer sliderAnimationTimer;

    private final String size_slider_name = "SIZE", speed_slider_name = "SPEED";
    private String current_slider;

    // final "Size" slider MIN,MAX, and TICK 
    public static final int MIN_SIZE_SLIDER_VAL = 10,
            MAX_SIZE_SLIDER_VAL = 500,
            INIT_SIZE_SLIDER_VAL = 250,
            SIZE_SLIDER_MINOR_TICK = 10,
            SIZE_SLIDER_MAJOR_TICK = 100;

    // final "Speed" slider MIN,MAX, and TICK
    public static final int MIN_SPEED_SLIDER_VAL = 10,
            MAX_SPEED_SLIDER_VAL = 1000,
            INIT_SPEED_SLIDER_VAL = 500,
            SPEED_SLIDER_MINOR_TICK = 10,
            SPEED_SLIDER_MAJOR_TICK = 250;

    // Creat enum Component
    private enum COMPONENT {
        // enable | disable
        BURGER, SORT, SHUFFLE, SLIDER,
        // visible | invisible
        SORT_VISIBILITY, STOP_VISIBILITY, SIZE_SLIDER_VISIBILITY;
    }

    private boolean wasPaused = false; // Set to false as default

    
    public Menu(MainScreen mainScreen, Rectangle rect) {
        super(rect);
        this.mainScreen = mainScreen;

        initComponent();
    }

    private void initComponent() {
        final int PADDING = CUSTOM_JPANEL_HEIGHT * 1 / 6; // Calculate padding based on the panel height
        final int FIXED_HEIGHT = CUSTOM_JPANEL_HEIGHT - (2 * PADDING), // Calculate fixed height by subtracting padding
                FIXED_WIDTH = FIXED_HEIGHT, // Set fixed width equal to fixed height to maintain a square dimension
                FIXED_Y = CUSTOM_JPANEL_HEIGHT / 2 - FIXED_HEIGHT / 2; // Center the component vertically
        final Font SLIDER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 13); // Define the font for the slider
    
        // width is important for identifying the dimension (square) of icons
        ImageIcon icons[] = setUpIcon(FIXED_HEIGHT); // Set up icons with the specified height
        final int BURGER_ENABLED_INDEX = 0, BURGER_DISABLED_INDEX = 1,
                SORT_ENABLED_INDEX = 2, SORT_DISABLED_INDEX = 3,
                SHUFFLE_ENABLED_INDEX = 4, SHUFFLE_DISABLED_INDEX = 5,
                STOP_ENABLED_INDEX = 6; // Define indices for different icons
    
        final String burger_tooltip = "ALGORITHM MENU", no_algorithm_tooltip = "PILIH ALGORITHMA NYA DULU",
                shuffle_tooltip = "SHUFFLE", stop_tooltip = "STOP"; // Define tooltips for buttons
    
        // ==== BUTTON ====
        AbstractAction burger_action = new AbstractAction("burger") {
            private static final long serialVersionUID = 1L;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen.startProcess(MainScreen.PROCESS.SIDEPANEL_ANIMATION); // Start side panel animation
                if (wasPaused)
                    resetSorting(); // Reset sorting if paused
                mainScreen.repaint(); // Repaint the main screen
            }
        };
    
        burger = new CustomJButton(new Rectangle(
                PADDING,
                FIXED_Y,
                FIXED_WIDTH, FIXED_HEIGHT), // Set button position and size
                burger_action,
                icons[BURGER_ENABLED_INDEX], // Set enabled icon
                icons[BURGER_DISABLED_INDEX], // Set disabled icon
                burger_tooltip); // Set tooltip
    
        AbstractAction sort_action = new AbstractAction("sort") {
            private static final long serialVersionUID = 1L;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!mainScreen.hasChosenAnAlgorithm()) {
                    sort.setToolTipText("Please choose an algorithm first :)"); // Show tooltip if no algorithm chosen
                    sort.createToolTip().setVisible(true);
                    return;
                } else {
                    sort.setToolTipText(""); // Clear tooltip if algorithm chosen
                }
    
                if (wasPaused) {
                    mainScreen.startProcess(MainScreen.PROCESS.RESUME); // Resume process if paused
                } else {
                    resetSorting(); // Reset sorting if not paused
                    mainScreen.startProcess(MainScreen.PROCESS.SORT); // Start sorting process
                }
    
                if (mainScreen.isSidePanelShown())
                    mainScreen.startProcess(MainScreen.PROCESS.SIDEPANEL_ANIMATION); // Start side panel animation if shown
    
                changeComponentState(COMPONENT.BURGER, COMPONENT.SHUFFLE, COMPONENT.SLIDER,
                        COMPONENT.SORT_VISIBILITY, COMPONENT.STOP_VISIBILITY); // Change component states
            }
        };
    
        sort = new CustomJButton(new Rectangle(burger.getX() + burger.getWidth() + PADDING,
                FIXED_Y,
                FIXED_WIDTH,
                FIXED_HEIGHT), // Set button position and size
                sort_action,
                icons[SORT_ENABLED_INDEX], // Set enabled icon
                icons[SORT_DISABLED_INDEX], // Set disabled icon
                no_algorithm_tooltip); // Set tooltip
    
        AbstractAction stop_action = new AbstractAction("stop") {
            private static final long serialVersionUID = 1L;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                wasPaused = true; // remains true until sortingPanel was reset
    
                changeComponentState(COMPONENT.BURGER, COMPONENT.SHUFFLE, COMPONENT.SLIDER,
                        COMPONENT.SORT_VISIBILITY, COMPONENT.STOP_VISIBILITY); // Change component states
    
                mainScreen.startProcess(MainScreen.PROCESS.PAUSE); // Start pause process
            }
        };
    
        stop = new CustomJButton(new Rectangle(burger.getX() + burger.getWidth() + PADDING,
                FIXED_Y,
                FIXED_WIDTH,
                FIXED_HEIGHT), // Set button position and size
                stop_action,
                icons[STOP_ENABLED_INDEX], // Set enabled icon
                stop_tooltip); // Set tooltip
    
        AbstractAction shuffle_action = new AbstractAction("shuffle") {
            private static final long serialVersionUID = 1L;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainScreen.isSidePanelShown())
                    mainScreen.startProcess(MainScreen.PROCESS.SIDEPANEL_ANIMATION); // Start side panel animation if shown
    
                changeComponentState(COMPONENT.BURGER, COMPONENT.SORT, COMPONENT.SHUFFLE,
                        COMPONENT.SIZE_SLIDER_VISIBILITY); // Change component states
                mainScreen.startProcess(MainScreen.PROCESS.SHUFFLE); // Start shuffle process
            }
        };
    
        shuffle = new CustomJButton(new Rectangle(sort.getX() + sort.getWidth() + PADDING,
                FIXED_Y,
                FIXED_WIDTH,
                FIXED_HEIGHT), // Set button position and size
                shuffle_action,
                icons[SHUFFLE_ENABLED_INDEX], // Set enabled icon
                icons[SHUFFLE_DISABLED_INDEX], // Set disabled icon
                shuffle_tooltip); // Set tooltip
    
        // ==== SLIDER ====
        current_slider = size_slider_name; // Set current slider name
        sliderName = new CustomJLabel(new Rectangle(shuffle.getX() + shuffle.getWidth() + PADDING,
                FIXED_Y,
                FIXED_WIDTH * 5 / 4, // increased width to see "SPEED"
                FIXED_HEIGHT), current_slider, SLIDER_FONT); // Set slider label position, size, and font
    
        UIManager.put("Slider.paintValue", false); // Disable default slider value painting
    
        sizeSlider = new CustomJSlider(
                new Rectangle(sliderName.getX() + sliderName.getWidth() + PADDING,
                        FIXED_Y,
                        FIXED_WIDTH * 2,
                        FIXED_HEIGHT), // Set slider position and size
                new DefaultBoundedRangeModel(INIT_SIZE_SLIDER_VAL, 0,
                        MIN_SIZE_SLIDER_VAL, MAX_SIZE_SLIDER_VAL), // Set slider range model
                SIZE_SLIDER_MINOR_TICK, SIZE_SLIDER_MAJOR_TICK); // Set slider tick spacing
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (mainScreen.isSidePanelShown())
                    mainScreen.startProcess(MainScreen.PROCESS.SIDEPANEL_ANIMATION); // Start side panel animation if shown
    
                resetSorting(); // Reset sorting
                int size_value = ((CustomJSlider) e.getSource()).getValue(); // Get slider value
                mainScreen.changeValue(size_slider_name, size_value); // Change main screen slider value
                sliderValue.setText(String.valueOf(size_value)); // Update slider value display
            }
        });
        sizeSlider.addMouseListener(new MouseListener() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sliderAnimationTimer.start(); // Start slider animation timer on mouse enter
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                sliderAnimationTimer.start(); // Start slider animation timer on mouse exit
            }
    
            @Override
            public void mouseClicked(MouseEvent e) {
            }
    
            @Override
            public void mousePressed(MouseEvent e) {
            }
    
            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
    
        speedSlider = new CustomJSlider(sizeSlider.getBounds(),
                new DefaultBoundedRangeModel(INIT_SPEED_SLIDER_VAL, 0,
                        MIN_SPEED_SLIDER_VAL, MAX_SPEED_SLIDER_VAL), // Set slider range model
                SPEED_SLIDER_MINOR_TICK, SPEED_SLIDER_MAJOR_TICK); // Set slider tick spacing
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int speed_value = ((CustomJSlider) e.getSource()).getValue(); // Get slider value
                mainScreen.changeValue(speed_slider_name, speed_value); // Change main screen slider value
                sliderValue.setText(String.valueOf(speed_value) + "ms"); // Update slider value display with unit
            }
        });
        speedSlider.addMouseListener(new MouseListener() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sliderAnimationTimer.start(); // Start slider animation timer on mouse enter
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                sliderAnimationTimer.start(); // Start slider animation timer on mouse exit
            }
    
            @Override
            public void mouseClicked(MouseEvent e) {
            }
    
            @Override
            public void mousePressed(MouseEvent e) {
            }
    
            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
    
        sliderValue = new CustomJLabel(new Rectangle(sizeSlider.getX(),
                FIXED_Y,
                FIXED_WIDTH * 3 / 2, // increased width to see "x.xx ms"
                FIXED_HEIGHT), // Set slider value label position and size
                String.valueOf(sizeSlider.getValue()), SLIDER_FONT); // Initialize slider value label with current value and font
    
        // ==== TITLE ====
        // x depends on the current width of CustomJSlider
        String showTitle = "<html><span style = \"font-weight: bold; color: #DDDDDD; font-size: 25px;\">Sorting Visualizer</span></html>"; // Define title text with HTML formatting
        title = new CustomJLabel(new Rectangle(sizeSlider.getX() + sizeSlider.getStartOfCoveredRegion(),
                FIXED_Y,
                CUSTOM_JPANEL_WIDTH - (sizeSlider.getX() + sizeSlider.getWidth() + PADDING),
                FIXED_HEIGHT), showTitle); // Set title label position, size, and text
    
        // initial state of components
        sort.setEnabled(false); // Disable sort button initially
        stop.setVisible(false); // Hide stop button initially
        speedSlider.setVisible(false); // Hide speed slider initially
    
        initAnimationTimer(); // Initialize animation timer
    
        // Add component to Menu panel
        add(burger); // Add burger button to the panel
        add(sort); // Add sort button to the panel
        add(shuffle); // Add shuffle button to the panel
        add(stop); // Add stop button to the panel
        add(sliderName); // Add slider name label to the panel
        add(sliderValue); // Add slider value label to the panel
        add(sizeSlider); // Add size slider to the panel
        add(speedSlider); // Add speed slider to the panel
        add(title); // Add title label to the panel
    }
    
    private void initAnimationTimer() {
        sliderAnimationTimer = new Timer(0, new ActionListener() { // Initialize the timer with a delay of 0 and an action listener
            @Override
            public void actionPerformed(ActionEvent e) {
                int startOfCoveredRegion = sizeSlider.isVisible() ? sizeSlider.getStartOfCoveredRegion()
                        : speedSlider.getStartOfCoveredRegion(); // Determine the starting  point of the covered region based on which slider is visible
    
                // sizeSlider and speedSlider have the same X coordinate relative to Menu panel.
                // Must add the X coordinate since the startOfCoveredRegion is relative to the slider's X coordinate and not on the panel.
                startOfCoveredRegion += sizeSlider.getX(); // Adjust the starting point to be relative to the panel
    
                boolean animationRunning = sizeSlider.isVisible() ? sizeSlider.isAnimationRunning()
                        : speedSlider.isAnimationRunning(); // Check if the animation is running for the visible slider
    
                if (animationRunning) {
                    sliderValue.setLocation(startOfCoveredRegion, sliderValue.getY()); // Move the slider value label to the new position
                    title.setLocation(sliderValue.getX() + sliderValue.getWidth(), title.getY()); // Move the title label accordingly
                } else
                    sliderAnimationTimer.stop(); // Stop the timer if the animation is not running
            }
        });
    }
    
    private ImageIcon[] setUpIcon(int dim) {
        String paths[] = {
            "/asset/burger-icon-enabled.png", 
            "/asset/burger-icon-disabled.png",
            "/asset/sort-icon-enabled.png", 
            "/asset/sort-icon-disabled.png",
            "/asset/shuffle-icon-enabled.png", 
            "/asset/shuffle-icon-disabled.png",
            "/asset/stop-icon-enabled.png"
        }; // Define the paths to the icon images
    
        ImageIcon icons[] = new ImageIcon[paths.length]; // Create an array to hold the icons
    
        // only occupy the 2 / 3 of the button
        final int ICON_WIDTH = dim * 2 / 3,
                ICON_HEIGHT = ICON_WIDTH; // Calculate the width and height of the icons
    
        for (int i = 0; i < icons.length; i++) {
            try {
                Image image = ImageIO.read(getClass()
                        .getResource(paths[i]))
                        .getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH); // Read and scale the image
                icons[i] = new ImageIcon(image); // Create an ImageIcon and store it in the array
            } catch (IOException e) {
                System.out.println("Failed to get Image File"); // Print an error message if the image file cannot be read
                e.printStackTrace();
            }
        }
    
        return icons; // Return the array of icons
    }
    
    private void changeComponentState(COMPONENT... components) {
        for (COMPONENT component : components) { // Iterate through the given components
            switch (component) {
                case BURGER:
                    burger.setEnabled(!burger.isEnabled()); // Toggle the enabled state of the burger button
                    break;
                case SORT:
                    // sort will remain disabled until there's an algorithm chosen.
                    // therefore, any attempt to change it (i.e. via shuffle), sort will remain disabled
                    if (!mainScreen.hasChosenAnAlgorithm())
                        sort.setEnabled(false); // Keep the sort button disabled if no algorithm is chosen
                    else
                        sort.setEnabled(!sort.isEnabled()); // Toggle the enabled state of the sort button if an algorithm is chosen
                    break;
                case SORT_VISIBILITY:
                    sort.setVisible(!sort.isVisible()); // Toggle the visibility of the sort button
                    break;
                case STOP_VISIBILITY:
                    stop.setVisible(!stop.isVisible()); // Toggle the visibility of the stop button
                    break;
                case SHUFFLE:
                    shuffle.setEnabled(!shuffle.isEnabled()); // Toggle the enabled state of the shuffle button
                    break;
                case SLIDER:
                    sizeSlider.setVisible(!sizeSlider.isVisible()); // Toggle the visibility of the size slider
                    speedSlider.setVisible(!speedSlider.isVisible()); // Toggle the visibility of the speed slider
    
                    current_slider = current_slider == size_slider_name ? speed_slider_name
                            : size_slider_name; // Switch the current slider name
                    sliderName.setText(current_slider); // Update the slider name label
    
                    int value = current_slider == size_slider_name ? sizeSlider.getValue()
                            : speedSlider.getValue(); // Get the value of the current slider
    
                    String strValue = current_slider == size_slider_name ? String.valueOf(value)
                            : String.valueOf(value) + "ms"; // Format the slider value
                    sliderValue.setText(strValue); // Update the slider value label
                    break;
                case SIZE_SLIDER_VISIBILITY:
                    sizeSlider.setVisible(!sizeSlider.isVisible()); // Toggle the visibility of the size slider
                    break;
                default:
                    System.out.println("Undefined Component"); // Print an error message for undefined components
                    break;
            }
        }
    }
    
    public void donePicking() {
        sort.setEnabled(true); // Enable the sort button
        sort.setToolTipText("SORT"); // Set the tooltip text for the sort button
    }
    
    public void doneShuffling() {
        changeComponentState(COMPONENT.BURGER, COMPONENT.SORT, COMPONENT.SHUFFLE,
                COMPONENT.SIZE_SLIDER_VISIBILITY); // Change the state of the components after shuffling
        resetSorting(); // Reset the sorting
    }
    
    public void doneSorting() {
        changeComponentState(COMPONENT.BURGER, COMPONENT.SHUFFLE, COMPONENT.SLIDER,
                COMPONENT.SORT_VISIBILITY, COMPONENT.STOP_VISIBILITY); // Change the state of the components after sorting
        resetSorting(); // Reset the sorting
    }
    
    public void resetSorting() {
        wasPaused = false; // Reset the paused state
        mainScreen.startProcess(MainScreen.PROCESS.RESET); // Start the reset process on the main screen
    }
}