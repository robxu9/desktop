    package com.github.axet.desktop;
    
    public class DesktopTest {
        public static void main(String[] args) {
            DesktopFolders d = Desktop.getDesktopFolders();
    
            System.out.println("Home: " + d.getHome());
            System.out.println("Documents: " + d.getDocuments());
            System.out.println("AppFolder: " + d.getAppData());
            System.out.println("Desktop: " + d.getDesktop());
            System.out.println("Downloads: " + d.getDownloads());
        }
    }
