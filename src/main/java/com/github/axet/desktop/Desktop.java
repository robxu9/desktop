package com.github.axet.desktop;

import java.io.File;

import com.github.axet.desktop.os.Windows;
import com.github.axet.desktop.os.mac.OSX;

public abstract class Desktop {

    public static Desktop getDesktop() {
        if (com.sun.jna.Platform.isWindows()) {
            return new Windows();
        }

        if (com.sun.jna.Platform.isMac()) {
            return new OSX();
        }

        return null;
    }

    // user application data folder
    abstract public File getAppData();

    // user home
    abstract public File getHome();

    // user my documents
    abstract public File getDocuments();

    // user downloads
    abstract public File getDownloads();

    public static void main(String[] args) {
        Desktop d = Desktop.getDesktop();
        
        System.out.println("Home: "+d.getHome());
        System.out.println("Documents: "+d.getDocuments());
        System.out.println("AppFolder: "+d.getAppData());
        System.out.println("Downloads: "+d.getDownloads());
    }
}
