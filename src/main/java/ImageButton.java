import javax.swing.*;
import java.awt.*;

// Create ImageButton Class (For Button with Background Images)
public class ImageButton extends JButton {
    private Image backgroundImage;

    public ImageButton(Image backgroundImage, String text) {
        super(text);
        this.backgroundImage = backgroundImage;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(CENTER);
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        super.paintComponent(g);
    }
}