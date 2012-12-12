package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSApplication extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSApplication");

    static Pointer sharedApplication = Runtime.INSTANCE.sel_getUid("sharedApplication");

    static Pointer isRunning = Runtime.INSTANCE.sel_getUid("isRunning");

    static Pointer isActive = Runtime.INSTANCE.sel_getUid("isActive");

    static Pointer run = Runtime.INSTANCE.sel_getUid("run");

    static Pointer requestUserAttention = Runtime.INSTANCE.sel_getUid("requestUserAttention:");

    static Pointer setApplicationIconImage = Runtime.INSTANCE.sel_getUid("setApplicationIconImage:");

    static Pointer cancelUserAttentionRequest = Runtime.INSTANCE.sel_getUid("cancelUserAttentionRequest:");

    static Pointer dockTile = Runtime.INSTANCE.sel_getUid("dockTile");

    public final static int NSCriticalRequest = 0;
    public final static int NSInformationalRequest = 10;

    public NSApplication() {
        super(Runtime.INSTANCE.objc_msgSend(klass, sharedApplication));

        retain();
    }

    public NSApplication(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
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
}