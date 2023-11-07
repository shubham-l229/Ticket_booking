package GUI_settings;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JScrollBar;

/**
 *
 * @author Aryan Mehta
 */
public class ScrollBar extends JScrollBar{
    public ScrollBar() {
        setUI(new ModernScrollbarUI());
        setPreferredSize(new Dimension(5, 5));
        setBackground(new Color(242, 242, 242));
        setUnitIncrement(20);
    }
}
