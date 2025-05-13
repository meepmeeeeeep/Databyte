// ImageButton.java

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
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }

        super.paintComponent(g);
    }
}