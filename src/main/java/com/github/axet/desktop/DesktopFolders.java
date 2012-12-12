package com.github.axet.desktop;

import java.io.File;

import com.github.axet.desktop.os.Linux;
import com.github.axet.desktop.os.mac.OSX;
import com.github.axet.desktop.os.win.Windows;

public interface DesktopFolders {

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
