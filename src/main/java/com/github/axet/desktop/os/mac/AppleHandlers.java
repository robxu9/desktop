package com.github.axet.desktop.os.mac;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.axet.desktop.os.mac.cocoa.NSApplication;
import com.github.axet.desktop.os.mac.cocoa.NSApplicationDelegate;
import com.github.axet.desktop.os.mac.cocoa.NSArray;
import com.github.axet.desktop.os.mac.cocoa.NSMenu;
import com.github.axet.desktop.os.mac.cocoa.NSMenuItem;
import com.github.axet.desktop.os.mac.cocoa.NSObject;
import com.github.axet.desktop.os.mac.cocoa.NSString;
import com.github.axet.desktop.os.mac.foundation.ApplicationServices;
import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public class AppleHandlers extends NSApplicationDelegate {

    //
    // public intarfaces
    //

    public static interface OpenFileHandler {
        public void openFile(File f);
    }

    public static interface QuitHandler {
        public void handleQuit();
    }

    public static interface OpenURIHandler {
        public void openURI(URI uri);
    }

    public static interface AppReOpenedListener {
        public void appReOpened();
    }

    public static interface AboutHandler {
        public void showAboutMenu();
    }

    // uri

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
        ApplicationServices.INSTANCE.LSSetDefaultHandlerForURLScheme(new NSString(s), new NSString(bundleId));
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
        ApplicationServices.INSTANCE.LSSetDefaultRoleHandlerForContentType(new NSString(s), -1, new NSString(bundleId));
    }

    public static boolean isAvailable() {
        return com.sun.jna.Platform.isMac();
    }

    // public methods

    static ArrayList<OpenFileHandler> files = new ArrayList<OpenFileHandler>();

    public void addOpenFileListener(OpenFileHandler e) {
        files.add(e);
    }

    public void removeOpenFileListener(OpenFileHandler e) {
        files.remove(e);
    }

    static ArrayList<QuitHandler> quit = new ArrayList<AppleHandlers.QuitHandler>();

    public void addQuitHandlerListener(QuitHandler q) {
        quit.add(q);
    }

    public void removeQuitHandlerListener(QuitHandler q) {
        quit.remove(q);
    }

    static ArrayList<OpenURIHandler> uri = new ArrayList<OpenURIHandler>();

    public void addOpenURIListener(OpenURIHandler e) {
        uri.add(e);
    }

    public void removeOpenURIListener(OpenURIHandler e) {
        uri.remove(e);
    }

    static ArrayList<AppReOpenedListener> re = new ArrayList<AppReOpenedListener>();

    public void addAppReOpenedListener(AppReOpenedListener e) {
        re.add(e);
    }

    public void removeAppReOpenedListener(AppReOpenedListener e) {
        re.remove(e);
    }

    static ArrayList<AboutHandler> ab = new ArrayList<AboutHandler>();

    public void addAboutListener(AboutHandler e) {
        ab.add(e);
    }

    public void removeAboutListener(AboutHandler e) {
        ab.remove(e);
    }

    //
    // register
    //

    final static Pointer registerKlass = Runtime.INSTANCE.objc_allocateClassPair(NSObject.klass,
            AppleHandlers.class.getSimpleName(), 0);

    // applicationShouldTerminate

    public interface NSApplicationTerminateReply {
        public final int NSTerminateCancel = 0;
        public final int NSTerminateNow = 1;
        public final int NSTerminateLater = 2;
    };

    final static Pointer registerApplicationShouldTerminateSelector = Runtime.INSTANCE
            .sel_registerName("applicationShouldTerminate:");

    public interface RegisterApplicationShouldTerminateAction extends StdCallCallback {
        public int callback(Pointer self, Pointer selector, Pointer sender);
    }

    final static RegisterApplicationShouldTerminateAction registerApplicationShouldTerminateImp = new RegisterApplicationShouldTerminateAction() {
        public int callback(Pointer self, Pointer selector, Pointer sender) {
            if (selector.equals(registerApplicationShouldTerminateSelector)) {
                for (QuitHandler q : new ArrayList<QuitHandler>(quit)) {
                    q.handleQuit();
                }
            }
            return NSApplicationTerminateReply.NSTerminateNow;
        }
    };

    // applicationOpenFile

    final static Pointer applicationOpenFile = Runtime.INSTANCE.sel_registerName("application:openFile:");

    public interface ApplicationOpenFileImp extends StdCallCallback {
        public boolean callback(Pointer self, Pointer selector, NSString file);
    }

    final static ApplicationOpenFileImp applicationOpenFileImp = new ApplicationOpenFileImp() {
        public boolean callback(Pointer self, Pointer selector, NSString file) {
            if (selector.equals(applicationOpenFile)) {
                for (OpenFileHandler q : new ArrayList<OpenFileHandler>(files)) {
                    q.openFile(new File(file.toString()));
                }
            }
            return true;
        }
    };

    // about menu

    final static Pointer aboutMenu = Runtime.INSTANCE.sel_registerName("aboutMenu");

    public interface AboutMenuImp extends StdCallCallback {
        public void callback(Pointer self, Pointer selector);
    }

    final static AboutMenuImp aboutMenuImp = new AboutMenuImp() {
        public void callback(Pointer self, Pointer selector) {
            if (selector.equals(aboutMenu)) {
                for (AboutHandler q : new ArrayList<AboutHandler>(ab)) {
                    q.showAboutMenu();
                }
            }
        }
    };

    // application:openFileWithoutUI:

    final static Pointer applicationOpenFileNo = Runtime.INSTANCE.sel_registerName("application:openFileWithoutUI:");

    public interface ApplicationOpenFileNoImp extends StdCallCallback {
        public boolean callback(Pointer self, Pointer selector, NSString file);
    }

    final static ApplicationOpenFileNoImp applicationOpenFileNoImp = new ApplicationOpenFileNoImp() {
        public boolean callback(Pointer self, Pointer selector, NSString file) {
            if (selector.equals(applicationOpenFileNo)) {
                for (OpenURIHandler q : new ArrayList<OpenURIHandler>(uri)) {
                    try {
                        q.openURI(new URI(file.toString()));
                    } catch (URISyntaxException e) {
                        // ignore
                    }
                }
            }
            return true;
        }
    };

    // applicationOpenFiles

    final static Pointer applicationOpenFiles = Runtime.INSTANCE.sel_registerName("application:openFiles:");

    public interface ApplicationOpenFilesImp extends StdCallCallback {
        public boolean callback(Pointer self, Pointer selector, NSArray file);
    }

    final static ApplicationOpenFilesImp applicationOpenFilesImp = new ApplicationOpenFilesImp() {
        public boolean callback(Pointer self, Pointer selector, NSArray file) {
            if (selector.equals(applicationOpenFiles)) {
                for (OpenFileHandler q : new ArrayList<OpenFileHandler>(files)) {
                    for (int i = 0; i < file.count(); i++) {
                        NSString s = new NSString(file.objectAtIndex(i));
                        q.openFile(new File(s.toString()));
                    }
                }
            }
            return true;
        }
    };

    static {
        if (!Runtime.INSTANCE.class_addMethod(registerKlass, registerApplicationShouldTerminateSelector,
                registerApplicationShouldTerminateImp, "i@:i"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, applicationOpenFile, applicationOpenFileImp, "B@:*"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, applicationOpenFileNo, applicationOpenFileNoImp, "B@:*"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, applicationOpenFiles, applicationOpenFilesImp, "v@:@"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, aboutMenu, aboutMenuImp, "v@:"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addProtocol(registerKlass, NSApplicationDelegate.protocol))
            throw new RuntimeException("problem initalizing class protocol");

        Runtime.INSTANCE.objc_registerClassPair(registerKlass);
    }

    //
    // class instances
    //

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass(AppleHandlers.class.getSimpleName());

    static Pointer action = Runtime.INSTANCE.sel_getUid("aboutMenu");

    //
    // members
    //

    public AppleHandlers() {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        NSApplication a = NSApplication.sharedApplication();
        a.setDelegate(this);

        // getting about menu. i hope here is better way to do so. (tag lookup
        // for example)
        NSMenu m = a.mainMenu();
        NSMenuItem mm = m.itemAtIndex(0);
        m = mm.submenu();
        mm = m.itemAtIndex(0);
        mm.setTarget(this);
        mm.setAction(action);
    }

    public AppleHandlers(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

}