package com.ExploBD.presentation.components;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;


public abstract class BaseCard<T> extends JPanel {
    
   
    protected static final Color DARK_BROWN = new Color(70, 58, 47);
    protected static final Color ORANGE = new Color(255, 153, 51);
    protected static final Color GOLD = new Color(255, 215, 0);
    protected static final Color LIGHT_CREAM = new Color(250, 245, 240);
    protected static final Color BROWN_BG = new Color(88, 74, 60);
    
    protected T data;
    protected Color backgroundColor = Color.WHITE;
    protected Color borderColor = new Color(200, 180, 160);
    
    public BaseCard(T data) {
        this.data = data;
        setLayout(new BorderLayout(10, 5));
        setBackground(backgroundColor);
        setBorder(createDefaultBorder());
        buildCard(); 
    }
    
    private Border createDefaultBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }
    
    
    protected final void buildCard() {
        removeAll();
        add(createLeftPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
    }
    
    
    protected abstract JPanel createLeftPanel();
    protected abstract JPanel createCenterPanel();
    protected abstract JPanel createRightPanel();
    
    
    protected void onDataChanged() {
        refresh();
    }
    
    
    protected JLabel createIconLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    protected JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    protected JButton createSmallButton(String text, Color bgColor, int size) {
        JButton btn = createActionButton(text, bgColor);
        btn.setPreferredSize(new Dimension(size, size));
        return btn;
    }
    
    
    public T getData() { return data; }
    
    public void setData(T newData) { 
        this.data = newData; 
        onDataChanged();
    }
    
    public void refresh() { 
        buildCard(); 
        revalidate(); 
        repaint(); 
    }
}