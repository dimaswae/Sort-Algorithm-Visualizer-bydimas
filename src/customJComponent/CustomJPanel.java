package customJComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class CustomJPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	protected int CUSTOM_JPANEL_WIDTH;
	protected int CUSTOM_JPANEL_HEIGHT;
	
	public CustomJPanel(Rectangle rect) {
		CUSTOM_JPANEL_WIDTH  = rect.width;
		CUSTOM_JPANEL_HEIGHT = rect.height;
		
		setBounds(rect);
		setLayout(null);
		setBackground(Color.BLACK);
	}
	
	public CustomJPanel(Dimension dim) {
		this(new Rectangle(0, 0, dim.width, dim.height));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(CUSTOM_JPANEL_WIDTH, CUSTOM_JPANEL_HEIGHT);
	}
}