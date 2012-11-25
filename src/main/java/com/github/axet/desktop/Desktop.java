package com.github.axet.desktop;

import java.io.File;

import com.github.axet.desktop.os.Linux;
import com.github.axet.desktop.os.mac.OSX;
import com.github.axet.desktop.os.win.Windows;

public abstract class Desktop {

    public static Desktop desktop() {
        if (com.sun.jna.Platform.isWindows()) {
            return new Windows();
        }

        if (com.sun.jna.Platform.isMac()) {
            return new OSX();
        }

        if (com.sun.jna.Platform.isLinux()) {
            return new Linux();
        }

        throw new RuntimeException("OS not supported");
    }

    // user application data folder
    abstract public File getAppData();

    // user home
    abstract public File getHome();

    // user my documents
    abstract public File getDocuments();

    // user downloads
    abstract public File getDownloads();

    // user desktop
    abstract public File getDesktop();

    public static void main(String[] args) {
        Desktop d = Desktop.desktop();

        System.out.println("Home: " + d.getHome());
        System.out.println("Documents: " + d.getDocuments());
        System.out.println("AppFolder: " + d.getAppData());
        System.out.println("Desktop: " + d.getDesktop());
        System.out.println("Downloads: " + d.getDownloads());
    }
}
