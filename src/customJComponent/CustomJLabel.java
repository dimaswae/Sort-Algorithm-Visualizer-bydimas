package customJComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class CustomJLabel extends JLabel {
  private static final long serialVersionUID = 1L;

  protected int CUSTOM_JLABEL_WIDTH;
  protected int CUSTOM_JLABEL_HEIGHT;

  // Setting Custom Label instance
  public CustomJLabel(Rectangle rect, ImageIcon icon, String text) {
    this.CUSTOM_JLABEL_WIDTH = rect.width;
    this.CUSTOM_JLABEL_HEIGHT = rect.height;

    setBounds(rect);
    setIcon(icon);
    setText(text);

    setOpaque(false);
    setForeground(Color.WHITE);
    setHorizontalAlignment(JLabel.CENTER);
    setVerticalAlignment(JLabel.CENTER);

  }

  // CustomJLabel instance with the specified rectangular bounds and text.
  public CustomJLabel(Rectangle rect, ImageIcon icon) {
    this(rect, icon, "");
  }

  // CustomJLabel with rectangular bounds, with no image, empty string for
  // content.
  public CustomJLabel(Rectangle rect, String text, Font font) {
    this(rect, null, text);
    setFont(font);
  }

  public CustomJLabel(Rectangle rect, String text) {
		this(rect, null, text);
	}

  public CustomJLabel(Rectangle rect) {
		this(rect, null, "");
	}

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(this.CUSTOM_JLABEL_WIDTH, this.CUSTOM_JLABEL_HEIGHT);
  }
}
