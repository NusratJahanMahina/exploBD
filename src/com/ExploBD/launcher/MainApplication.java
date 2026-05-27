package com.ExploBD.launcher;

import com.ExploBD.presentation.frames.Intro;
import com.ExploBD.presentation.frames.Intro;
import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
       
        SwingUtilities.invokeLater(() -> {
            Intro splash = new Intro();
            splash.setVisible(true);
            
           
        });
    }
}