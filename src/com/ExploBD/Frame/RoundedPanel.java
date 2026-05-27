package com.ExploBD.Frame;
import java.awt.*;
import javax.swing.*;

public class RoundedPanel extends JPanel {
    public RoundedPanel() {
        setOpaque(false); 
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
       
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
        g2.dispose();
    }
}