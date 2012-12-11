package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class NSMenuItem extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSMenuItem");

    static Pointer setTitle = Runtime.INSTANCE.sel_getUid("setTitle:");

    public NSMenuItem() {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        retain();
    }

    public NSMenuItem(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
    }

    public void setTitle(NSString i) {
        Runtime.INSTANCE.objc_msgSend(this, setTitle, i);
    }
}