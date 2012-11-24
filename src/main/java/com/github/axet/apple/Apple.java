package com.github.axet.apple;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.File;

import com.apple.eawt.AppEvent.AppReOpenedEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.OpenURIEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.AppReOpenedListener;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.sun.jna.Pointer;

import fundations.ApplicationServices;
import fundations.CoreFoundation;

public class Apple {

    public Apple() {
        Application a = Application.getApplication();

        a.setOpenFileHandler(new OpenFilesHandler() {
            public void openFiles(OpenFilesEvent e) {
                for (File f : e.getFiles())
                    ;
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                return false;
            }
        });

        // a.setDockIconBadge("1");

        a.addAppEventListener(new AppReOpenedListener() {
            public void appReOpened(AppReOpenedEvent e) {
            }
        });

        a.setQuitHandler(new QuitHandler() {
            public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
                arg1.performQuit();
            }
        });

        a.setOpenURIHandler(new com.apple.eawt.OpenURIHandler() {
            public void openURI(OpenURIEvent e) {
            }
        });

        Pointer bundle = CoreFoundation.INSTANCE.CFBundleGetMainBundle();
        CFStringRef s = CoreFoundation.INSTANCE.CFBundleGetIdentifier(bundle);
    }

    static void setUrlType(String s, String bundleId) {
        ApplicationServices.INSTANCE.LSSetDefaultHandlerForURLScheme(CFStringRef.CFSTR(s), CFStringRef.CFSTR(bundleId));
    }

    static void setFileType(String s, String bundleId) {
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

}
