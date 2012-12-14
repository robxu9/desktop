package com.github.axet.desktop.apple;

import javax.swing.JFrame;

import com.github.axet.desktop.os.mac.AppleHandlers;

public class AppDelegate {
    AppleHandlers d = new AppleHandlers();

    JFrame m = new JFrame("test");

    public AppDelegate() {
        m.setVisible(true);

        d.addQuitHandlerListener(new AppleHandlers.QuitHandler() {
            @Override
            public void handleQuit() {
                m.setVisible(false);
                m.dispose();
            }
        });
    }

    public static void main(String[] args) {
        new AppDelegate();
    }
}
