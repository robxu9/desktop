package com.github.axet.desktop.os.mac;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.axet.desktop.os.mac.cocoa.NSApplication;
import com.github.axet.desktop.os.mac.cocoa.NSApplicationDelegate;
import com.github.axet.desktop.os.mac.cocoa.NSObject;
import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class ApplicationDelegate extends NSApplicationDelegate {

    public static interface OpenFileHandler {
        public void openFile(File f);
    }

    static ArrayList<OpenFileHandler> files = new ArrayList<OpenFileHandler>();

    public void addOpenFileListener(OpenFileHandler e) {
        files.add(e);
    }

    public void removeOpenFileListener(OpenFileHandler e) {
        files.remove(e);
    }

    static ArrayList<QuitHandler> quit = new ArrayList<ApplicationDelegate.QuitHandler>();

    public static interface QuitHandler {
        public void handleQuit();
    }

    public void addQuitHandlerListener(QuitHandler q) {
        quit.add(q);
    }

    public void removeQuitHandlerListener(QuitHandler q) {
        quit.remove(q);
    }

    public static interface OpenURIHandler {
        public void openURI(URI uri);
    }

    public static interface AppReOpenedListener {
        public void appReOpened();
    }

    //
    // register
    //
    
    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass(OSXSysTrayAction.class.getSimpleName());

    static HashMap<Long, ApplicationDelegate> map = new HashMap<Long, ApplicationDelegate>();

    //
    // applicationOpenFile
    //
    final static Pointer applicationOpenFileSelector = Runtime.INSTANCE.sel_registerName("application:openFile:");
    final static Pointer applicationOpenFileMethod = Runtime.INSTANCE.class_getInstanceMethod(klass,
            applicationOpenFileSelector);
    ApplicationOpenFile applicationOpenFileImp = new ApplicationOpenFile() {
        @Override
        public void callback(Pointer self) {
        }
    };

    public interface ApplicationOpenFile extends StdCallCallback {
        public void callback(Pointer self);
    }

    //
    //
    //

    final static Pointer applicationOpenFilesSelector = Runtime.INSTANCE.sel_registerName("application:openFiles:");
    final static Pointer applicationOpenFilesMethod = Runtime.INSTANCE.class_getInstanceMethod(klass,
            applicationOpenFilesSelector);

    //
    //
    //

    final static Pointer applicationShouldTerminateSelector = Runtime.INSTANCE
            .sel_registerName("applicationShouldTerminate:");
    final static Pointer applicationShouldTerminateMethod = Runtime.INSTANCE.class_getInstanceMethod(klass,
            applicationShouldTerminateSelector);

    public interface ApplicationShouldTerminate extends StdCallCallback {
        public void callback(Pointer self);
    }

    ApplicationShouldTerminate applicationShouldTerminateImp = new ApplicationShouldTerminate() {
        @Override
        public void callback(Pointer self) {
            for (QuitHandler q : quit) {
                q.handleQuit();
            }
        }
    };

    public interface Action extends StdCallCallback {
        public void callback(Pointer self, Pointer selector);
    }

    //
    // natives
    //
    
    public ApplicationDelegate() {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        map.put(Pointer.nativeValue(this), this);

        Runtime.INSTANCE.method_setImplementation(applicationOpenFileMethod, applicationOpenFileImp);
        Runtime.INSTANCE.method_setImplementation(applicationShouldTerminateMethod, applicationShouldTerminateImp);

        NSApplication a = NSApplication.sharedApplication();
        a.setDelegate(this);

    }

    public ApplicationDelegate(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    protected void finalize() throws Throwable {
        map.remove(Pointer.nativeValue(this));
    }
}