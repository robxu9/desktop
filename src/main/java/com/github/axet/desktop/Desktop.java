package com.github.axet.desktop;

import com.github.axet.desktop.os.Linux;
import com.github.axet.desktop.os.mac.OSX;
import com.github.axet.desktop.os.mac.OSXSysTray;
import com.github.axet.desktop.os.win.Windows;
import com.github.axet.desktop.os.win.WindowsSysTray;

public abstract class Desktop {

    static DesktopFolders desktopFolders = null;
    static DesktopSysTray desktopSysTray = null;

    public static DesktopFolders getDesktopFolders() {
        if (desktopFolders == null) {
            if (com.sun.jna.Platform.isWindows()) {
                desktopFolders = new Windows();
            }

            if (com.sun.jna.Platform.isMac()) {
                desktopFolders = new OSX();
            }

            if (com.sun.jna.Platform.isLinux()) {
                desktopFolders = new Linux();
            }

            if (desktopFolders == null)
                throw new RuntimeException("OS not supported");
        }

        return desktopFolders;
    }

    public static DesktopSysTray getDesktopSysTray() {
        if (desktopSysTray == null) {
            if (com.sun.jna.Platform.isWindows()) {
                desktopSysTray = new WindowsSysTray();
            }

            if (com.sun.jna.Platform.isMac()) {
                desktopSysTray = new OSXSysTray();
            }

            if (desktopSysTray == null)
                throw new RuntimeException("OS not supported");
        }

        return desktopSysTray;
    }

}
