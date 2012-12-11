package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class NSImage extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSImage");

    static Pointer initWithData = Runtime.INSTANCE.sel_getUid("initWithData:");

    public NSImage(NSData data) {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        Runtime.INSTANCE.objc_msgSend(this, initWithData, data);

        retain();
    }

    public NSImage(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
    }

}