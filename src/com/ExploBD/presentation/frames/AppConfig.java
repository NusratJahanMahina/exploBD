package com.ExploBD.presentation.frames;




import javax.swing.JFrame;
import java.awt.Dimension;

public class AppConfig {
    public static int windowState = JFrame.NORMAL;
    public static Dimension lastSize = new Dimension(800, 600);
    public static boolean isFirstRun = true;

   
    public static void applyAndTrack(JFrame frame) {

        if (windowState == JFrame.MAXIMIZED_BOTH) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else if (!isFirstRun) {
            frame.setSize(lastSize);
            frame.setLocationRelativeTo(null);
        }

        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                windowState = frame.getExtendedState();
                if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    lastSize = frame.getSize();
                }
                isFirstRun = false;
            }
        });
    }
}