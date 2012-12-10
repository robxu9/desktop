package com.github.axet.desktop;

import java.io.File;

import com.github.axet.desktop.os.Linux;
import com.github.axet.desktop.os.mac.OSX;
import com.github.axet.desktop.os.win.Windows;

public abstract class Desktop {

    static Desktop desktop = null;

    public static Desktop desktop() {
        if (desktop == null) {
            if (com.sun.jna.Platform.isWindows()) {
                desktop = new Windows();
            }

            if (com.sun.jna.Platform.isMac()) {
                desktop = new OSX();
            }

            if (com.sun.jna.Platform.isLinux()) {
                desktop = new Linux();
            }

            if (desktop == null)
                throw new RuntimeException("OS not supported");
        }

        return desktop;
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
}
