package com.github.axet.desktop.os.mac;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.github.axet.desktop.os.mac.cocoa.AEEventClass;
import com.github.axet.desktop.os.mac.cocoa.AEEventID;
import com.github.axet.desktop.os.mac.cocoa.NSAppleEventDescriptor;
import com.github.axet.desktop.os.mac.cocoa.NSAppleEventManager;
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

// unable to use proper callbacks values.
//
// https://github.com/twall/jna/issues/168

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

    public static interface SettingsHandler {
        public void showSettingsMenu();
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

        sbuscribeToFiles();
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

        subscribeToURI();
    }

    public static class OpenFilesHandlerClassHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("openFiles")) {
                final Class<?> openFilesEventClass = Class.forName("com.apple.eawt.AppEvent$OpenFilesEvent");
                final Method getFiles = openFilesEventClass.getMethod("getFiles");

                Object e = args[0];

                try {
                    @SuppressWarnings("unchecked")
                    List<File> ff = (List<File>) getFiles.invoke(e);
                    for (OpenFileHandler q : new ArrayList<OpenFileHandler>(files)) {
                        for (File f : ff) {
                            q.openFile(f);
                        }
                    }
                } catch (RuntimeException ee) {
                    throw ee;
                } catch (Exception ee) {
                    throw new RuntimeException(ee);
                }
            }
            return null;
        }
    }

    void sbuscribeToFiles() {
        // we shall subscribe to java events, since first event already has been
        // eaten by Apple Java Wrapper

        try {
            final Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
            final Class<?> openFilesHandlerClass = Class.forName("com.apple.eawt.OpenFilesHandler");
            final Method getApplication = applicationClass.getMethod("getApplication");
            final Object application = getApplication.invoke(null);
            final Method setOpenFileHandler = applicationClass.getMethod("setOpenFileHandler", openFilesHandlerClass);

            ClassLoader openFilesHandlerClassLoader = openFilesHandlerClass.getClassLoader();
            OpenFilesHandlerClassHandler openFilesHandlerHandler = new OpenFilesHandlerClassHandler();
            Object openFilesHandlerObject = Proxy.newProxyInstance(openFilesHandlerClassLoader,
                    new Class<?>[] { openFilesHandlerClass }, openFilesHandlerHandler);

            setOpenFileHandler.invoke(application, openFilesHandlerObject);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class OpenURIHandlerHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("openURI")) {
                final Class<?> openURIEventClass = Class.forName("com.apple.eawt.AppEvent$OpenURIEvent");
                final Method getURI = openURIEventClass.getMethod("getURI");

                Object e = args[0];

                try {
                    for (OpenURIHandler q : new ArrayList<OpenURIHandler>(uri)) {
                        q.openURI((URI) getURI.invoke(e));
                    }
                } catch (RuntimeException ee) {
                    throw ee;
                } catch (Exception ee) {
                    throw new RuntimeException(ee);
                }
            }
            return null;
        }
    }

    void subscribeToURI() {
        NSAppleEventManager ev = NSAppleEventManager.sharedAppleEventManager();

        ev.setEventHandlerAndSelectorForEventClassAndEventID(this, getURL, AEEventClass.kInternetEventClass,
                AEEventID.kAEGetURL);

        // we shall subscribe to java events, since first event already has been
        // eaten by Apple Java Wrapper
        try {
            final Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
            final Method getApplication = applicationClass.getMethod("getApplication");
            final Object application = getApplication.invoke(null);
            final Class<?> openURIHandlerClass = Class.forName("com.apple.eawt.OpenURIHandler");
            final Method setOpenURIHandler = applicationClass.getMethod("setOpenURIHandler", openURIHandlerClass);
            final ClassLoader openURIHandlerClassLoader = openURIHandlerClass.getClassLoader();
            OpenURIHandlerHandler openURIHandlerHandler = new OpenURIHandlerHandler();

            Object openFilesHandlerObject = Proxy.newProxyInstance(openURIHandlerClassLoader,
                    new Class<?>[] { openURIHandlerClass }, openURIHandlerHandler);

            setOpenURIHandler.invoke(application, openFilesHandlerObject);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

    static ArrayList<SettingsHandler> st = new ArrayList<SettingsHandler>();

    public void addSettingsListener(SettingsHandler e) {
        st.add(e);
    }

    public void removeSettingsListener(SettingsHandler e) {
        st.remove(e);
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

    // getURL

    final static Pointer getURLRegister = Runtime.INSTANCE.sel_registerName("getURL");

    public interface GetURLAction extends StdCallCallback {
        public void callback(Pointer self, Pointer selector, Pointer event, Pointer replyEvent);
    }

    final static GetURLAction getURLActionImp = new GetURLAction() {
        public void callback(Pointer self, Pointer selector, final Pointer event, Pointer replyEvent) {
            if (selector.equals(getURLRegister)) {
                for (OpenURIHandler q : new ArrayList<OpenURIHandler>(uri)) {
                    try {
                        NSAppleEventDescriptor e = new NSAppleEventDescriptor(event);
                        if (e.numberOfItems() > 0) {
                            NSAppleEventDescriptor d = e.descriptorAtIndex(1);
                            final NSString s = d.stringValue();
                            q.openURI(new URI(s.toString()));
                        }
                    } catch (URISyntaxException e) {
                        // ignore
                    }
                }
            }
        }
    };

    // about menu

    final static Pointer aboutMenuRegister = Runtime.INSTANCE.sel_registerName("aboutMenu");

    public interface AboutMenuImp extends StdCallCallback {
        public void callback(Pointer self, Pointer selector);
    }

    final static AboutMenuImp aboutMenuImp = new AboutMenuImp() {
        public void callback(Pointer self, Pointer selector) {
            if (selector.equals(aboutMenuRegister)) {
                for (AboutHandler q : new ArrayList<AboutHandler>(ab)) {
                    q.showAboutMenu();
                }
            }
        }
    };

    // settings menu

    final static Pointer settingsMenuRegister = Runtime.INSTANCE.sel_registerName("settingsMenu");

    public interface SettingsMenuImp extends StdCallCallback {
        public void callback(Pointer self, Pointer selector);
    }

    final static AboutMenuImp settingsMenuImp = new AboutMenuImp() {
        public void callback(Pointer self, Pointer selector) {
            if (selector.equals(settingsMenuRegister)) {
                for (SettingsHandler q : new ArrayList<SettingsHandler>(st)) {
                    q.showSettingsMenu();
                }
            }
        }
    };

    // applicationOpenFile

    final static Pointer applicationOpenFile = Runtime.INSTANCE.sel_registerName("application:openFile:");

    public interface ApplicationOpenFileImp extends StdCallCallback {
        public boolean callback(Pointer self, Pointer selector, Pointer app, Pointer file);
    }

    final static ApplicationOpenFileImp applicationOpenFileImp = new ApplicationOpenFileImp() {
        public boolean callback(Pointer self, Pointer selector, Pointer app, Pointer file) {
            if (selector.equals(applicationOpenFile)) {
                for (OpenFileHandler q : new ArrayList<OpenFileHandler>(files)) {
                    q.openFile(new File(file.toString()));
                }
            }
            return true;
        }
    };

    // application:openFileWithoutUI:

    final static Pointer applicationOpenFileNo = Runtime.INSTANCE.sel_registerName("application:openFileWithoutUI:");

    public interface ApplicationOpenFileNoImp extends StdCallCallback {
        public boolean callback(Pointer self, Pointer selector, Pointer app, Pointer file);
    }

    final static ApplicationOpenFileNoImp applicationOpenFileNoImp = new ApplicationOpenFileNoImp() {
        public boolean callback(Pointer self, Pointer selector, Pointer app, Pointer file) {
            if (selector.equals(applicationOpenFileNo)) {
                for (OpenFileHandler q : new ArrayList<OpenFileHandler>(files)) {
                    q.openFile(new File(new NSString(file).toString()));
                }
            }
            return true;
        }
    };

    // applicationOpenFiles

    final static Pointer applicationOpenFiles = Runtime.INSTANCE.sel_registerName("application:openFiles:");

    public interface ApplicationOpenFilesImp extends StdCallCallback {
        public void callback(Pointer self, Pointer selector, Pointer app, Pointer file);
    }

    final static ApplicationOpenFilesImp applicationOpenFilesImp = new ApplicationOpenFilesImp() {
        public void callback(Pointer self, Pointer selector, Pointer app, Pointer file) {
            if (selector.equals(applicationOpenFiles)) {
                NSArray a = new NSArray(file);
                for (OpenFileHandler q : new ArrayList<OpenFileHandler>(files)) {
                    for (int i = 0; i < a.count(); i++) {
                        NSString s = new NSString(a.objectAtIndex(i));
                        q.openFile(new File(s.toString()));
                    }
                }
            }
        }
    };

    // applicationOpenFiles

    final static Pointer applicationReOpen = Runtime.INSTANCE
            .sel_registerName("applicationShouldHandleReopen:hasVisibleWindows:");

    public interface ApplicationReOpen extends StdCallCallback {
        public boolean callback(Pointer self, Pointer selector, Pointer app, boolean file);
    }

    final static ApplicationReOpen applicationReOpenImp = new ApplicationReOpen() {
        public boolean callback(Pointer self, Pointer selector, Pointer app, boolean file) {
            if (selector.equals(applicationReOpen)) {
                for (AppReOpenedListener q : new ArrayList<AppReOpenedListener>(re)) {
                    q.appReOpened();
                }
            }

            return true;
        }
    };

    static {
        if (!Runtime.INSTANCE.class_addMethod(registerKlass, registerApplicationShouldTerminateSelector,
                registerApplicationShouldTerminateImp, "i@:i"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, applicationOpenFile, applicationOpenFileImp, "B@:@*"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, applicationOpenFileNo, applicationOpenFileNoImp, "B@:@*"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, applicationOpenFiles, applicationOpenFilesImp, "v@:@@"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, applicationReOpen, applicationReOpenImp, "v@:@@"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, aboutMenuRegister, aboutMenuImp, "v@:"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, settingsMenuRegister, settingsMenuImp, "v@:"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addMethod(registerKlass, getURLRegister, getURLActionImp, "v@:@@"))
            throw new RuntimeException("problem initalizing class");

        if (!Runtime.INSTANCE.class_addProtocol(registerKlass, NSApplicationDelegate.protocol))
            throw new RuntimeException("problem initalizing class protocol");

        Runtime.INSTANCE.objc_registerClassPair(registerKlass);
    }

    //
    // class instances
    //

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass(AppleHandlers.class.getSimpleName());

    static Pointer aboutMenu = Runtime.INSTANCE.sel_getUid("aboutMenu");
    static Pointer settingsMenu = Runtime.INSTANCE.sel_getUid("settingsMenu");
    static Pointer getURL = Runtime.INSTANCE.sel_getUid("getURL");

    //
    // members
    //

    private AppleHandlers() {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        NSApplication a = NSApplication.sharedApplication();
        a.setDelegate(this);

        {
            // fixing about menu. i hope here is better way to do so. (tag
            // lookup for example)
            NSMenu m = a.mainMenu();
            NSMenuItem mm = m.itemAtIndex(0);
            m = mm.submenu();
            NSMenuItem mmm = m.itemAtIndex(0);
            mmm.setTarget(this);
            mmm.setAction(aboutMenu);
        }

        {
            // fixing about menu. i hope here is better way to do so. (tag
            // lookup for example)
            NSMenu m = a.mainMenu();
            NSMenuItem mm = m.itemAtIndex(0);
            m = mm.submenu();
            NSMenuItem mmm = NSMenuItem.initWithTitleActionKeyEquivalent(new NSString("Preferences…"), null,
                    new NSString(","));
            mmm.setTarget(this);
            mmm.setAction(settingsMenu);
            m.insertItemAtIndex(mmm, 1);
        }
    }

    private AppleHandlers(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    static AppleHandlers ah = null;

    static public AppleHandlers getAppleHandlers() {
        if (ah == null)
            ah = new AppleHandlers();
        return ah;
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

}