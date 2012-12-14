package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSApplication extends NSObject {

    public final static int NSCriticalRequest = 0;
    public final static int NSInformationalRequest = 10;

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSApplication");
    static Pointer sharedApplication = Runtime.INSTANCE.sel_getUid("sharedApplication");
    static Pointer isRunning = Runtime.INSTANCE.sel_getUid("isRunning");
    static Pointer isActive = Runtime.INSTANCE.sel_getUid("isActive");
    static Pointer run = Runtime.INSTANCE.sel_getUid("run");
    static Pointer requestUserAttention = Runtime.INSTANCE.sel_getUid("requestUserAttention:");
    static Pointer setApplicationIconImage = Runtime.INSTANCE.sel_getUid("setApplicationIconImage:");
    static Pointer cancelUserAttentionRequest = Runtime.INSTANCE.sel_getUid("cancelUserAttentionRequest:");
    static Pointer dockTile = Runtime.INSTANCE.sel_getUid("dockTile");
    static Pointer runModalForWindow = Runtime.INSTANCE.sel_getUid("runModalForWindow:");
    static Pointer delegate = Runtime.INSTANCE.sel_getUid("delegate");
    static Pointer setDelegate = Runtime.INSTANCE.sel_getUid("setDelegate:");
    static Pointer terminate = Runtime.INSTANCE.sel_getUid("terminate:");
    static Pointer mainMenu = Runtime.INSTANCE.sel_getUid("mainMenu");

    public static NSApplication sharedApplication() {
        return new NSApplication(Runtime.INSTANCE.objc_msgSend(klass, sharedApplication));
    }

    public NSApplication(long l) {
        super(l);
    }

    public NSApplication(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public boolean isRunning() {
        return Runtime.INSTANCE.objc_msgSend(this, isRunning) == 1;
    }

    public boolean isActive() {
        return Runtime.INSTANCE.objc_msgSend(this, isActive) == 1;
    }

    public void run() {
        Runtime.INSTANCE.objc_msgSend(this, run);
    }

    public long requestUserAttention(int type) {
        return Runtime.INSTANCE.objc_msgSend(this, requestUserAttention, type);
    }

    public NSDocTile dockTile() {
        return new NSDocTile(Runtime.INSTANCE.objc_msgSend(this, dockTile));
    }

    public void setApplicationIconImage(NSImage image) {
        Runtime.INSTANCE.objc_msgSend(this, setApplicationIconImage, image);
    }

    public void cancelUserAttentionRequest(long code) {
        Runtime.INSTANCE.objc_msgSend(this, cancelUserAttentionRequest, code);
    }

    public long runModalForWindow(NSWindow w) {
        return Runtime.INSTANCE.objc_msgSend(this, cancelUserAttentionRequest, w);
    }

    public NSApplicationDelegate delegate() {
        return new NSApplicationDelegate(Runtime.INSTANCE.objc_msgSend(this, delegate));
    }

    public void setDelegate(NSApplicationDelegate d) {
        Runtime.INSTANCE.objc_msgSend(this, setDelegate, d);
    }

    public void terminate(Pointer sender) {
        Runtime.INSTANCE.objc_msgSend(this, terminate, sender);
    }

    public NSMenu mainMenu() {
        return new NSMenu(Runtime.INSTANCE.objc_msgSend(this, mainMenu));
    }
}