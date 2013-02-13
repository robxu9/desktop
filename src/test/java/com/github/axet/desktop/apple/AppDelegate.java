package com.github.axet.desktop.apple;

import java.io.File;
import java.net.URI;

import javax.swing.JFrame;

import com.github.axet.desktop.os.mac.AppleHandlers;

public class AppDelegate {
    AppleHandlers d = AppleHandlers.getAppleHandlers();

    JFrame m = new JFrame("test");

    public AppDelegate() {
        m.setVisible(true);

        d.addOpenFileListener(new AppleHandlers.OpenFileHandler() {
            @Override
            public void openFile(File f) {
            }
        });

        d.addOpenURIListener(new AppleHandlers.OpenURIHandler() {
            @Override
            public void openURI(URI uri) {
            }
        });
    }

    public static void main(String[] args) {
        new AppDelegate();
    }
}
