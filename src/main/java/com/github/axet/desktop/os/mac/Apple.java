package com.github.axet.desktop.os.mac;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import com.github.axet.desktop.os.mac.foundation.ApplicationServices;

public class Apple {

    public static interface OpenFilesHandler {
        public void openFiles(List<File> e);
    }

    public static interface QuitHandler {
        /**
         * @return true - will close the app, false - ignore Apple quit request
         */
        public boolean handleQuitRequest();
    }

    public static interface OpenURIHandler {
        public void openURI(URI uri);
    }

    public static interface AppReOpenedListener {
        public void appReOpened();
    }

    static Class<?> ApplicationClass;
    static Method setDockIconBadge;
    static Method setOpenFileHandler;
    static Class<?> OpenFilesHandlerClass;

    static Class<?> AppEventClass;

    static Class<?> OpenFilesEventClass;
    static Method openFiles;

    {
        try {
            AppEventClass = Class.forName("com.apple.eawt.AppEvent");
            OpenFilesEventClass = Class.forName("com.apple.eawt.AppEvent$OpenFilesEvent");
            OpenFilesHandlerClass = Class.forName("com.apple.eawt.OpenFilesHandler");
            openFiles = OpenFilesHandlerClass.getMethod("openFiles", OpenFilesEventClass);

            ApplicationClass = Class.forName("com.apple.eawt.Application");
            setDockIconBadge = ApplicationClass.getMethod("setDockIconBadge", String.class);
            setOpenFileHandler = ApplicationClass.getMethod("setOpenFileHandler", OpenFilesHandlerClass);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Apple() {
    }

    /**
     * Sets the user’s preferred default handler for the specified URL scheme.
     * 
     * URL handling capability is determined according to the value of the
     * CFBundleURLTypes key in an application’s Info.plist. For information on
     * the CFBundleURLTypes key, see the section “CFBundleURLTypes” in OS X
     * Runtime Configuration Guidelines.
     * 
     * Browser will open your app, when uses clicks on the web page url (magnet
     * url for example).
     * 
     * @param s
     *            - URL scheme, ex: "magnet" for "magnet://some_magnet_link"
     * @param bundleId
     *            - bundle id
     */
    public static void setUrlType(String s, String bundleId) {
        ApplicationServices.INSTANCE.LSSetDefaultHandlerForURLScheme(CFStringRef.CFSTR(s), CFStringRef.CFSTR(bundleId));
    }

    /**
     * Sets the user’s preferred default handler for the specified content type
     * in the specified roles.
     * 
     * Call LSCopyDefaultRoleHandlerForContentType to get the current setting of
     * the user’s preferred default handler for a specified content type.
     * 
     * It helps browser to show proper download dialog which asks user which App
     * to use to open this file you are downloading.
     * 
     * @param s
     *            contenet handler. ex: "org.bittorrent.torrent", "torrent"
     * @param bundleId
     *            - bundle ID
     */
    public static void setFileType(String s, String bundleId) {
        ApplicationServices.INSTANCE.LSSetDefaultRoleHandlerForContentType(CFStringRef.CFSTR(s), -1,
                CFStringRef.CFSTR(bundleId));
    }

    public static boolean isAvailable() {
        try {
            Class.forName("com.apple.eawt.Application");
            return true;
        } catch (ClassNotFoundException e) {
        }

        return false;
    }

    public void setOpenFileHandler(final OpenFilesHandler o) {
        try {
            com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
            a.setOpenFileHandler(new com.apple.eawt.OpenFilesHandler() {
                public void openFiles(com.apple.eawt.AppEvent.OpenFilesEvent arg0) {
                    o.openFiles(arg0.getFiles());
                }
            });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setDockIconBadge(String s) {
        com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
        a.setDockIconBadge(s);
    }

    public void appReOpenedListener(final AppReOpenedListener r) {
        com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
        a.addAppEventListener(new com.apple.eawt.AppReOpenedListener() {
            public void appReOpened(com.apple.eawt.AppEvent.AppReOpenedEvent e) {
                r.appReOpened();
            }
        });
    }

    public void setQuitHandler(final QuitHandler q) {
        com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
        a.setQuitHandler(new com.apple.eawt.QuitHandler() {
            public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent arg0, com.apple.eawt.QuitResponse arg1) {
                if (q.handleQuitRequest())
                    arg1.performQuit();
            }
        });
    }

    public void setOpenURIHandler(final OpenURIHandler q) {
        com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
        a.setOpenURIHandler(new com.apple.eawt.OpenURIHandler() {
            public void openURI(com.apple.eawt.AppEvent.OpenURIEvent e) {
                q.openURI(e.getURI());
            }
        });
    }
}
