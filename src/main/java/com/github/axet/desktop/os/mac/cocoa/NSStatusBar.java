package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSStatusBar extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSStatusBar");

    static Pointer systemStatusBar = Runtime.INSTANCE.sel_getUid("systemStatusBar");

    static Pointer statusItemWithLength = Runtime.INSTANCE.sel_getUid("statusItemWithLength:");

    static Pointer removeStatusItem = Runtime.INSTANCE.sel_getUid("removeStatusItem:");

    public NSStatusBar() {
        super(Runtime.INSTANCE.objc_msgSend(klass, systemStatusBar));

        retain();
    }

    public NSStatusBar(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
    }

    public NSStatusItem statusItemWithLength(long i) {
        return new NSStatusItem(Runtime.INSTANCE.objc_msgSend(this, statusItemWithLength, i));
    }

    public void removeStatusItem(NSStatusItem i) {
        Runtime.INSTANCE.objc_msgSend(this, removeStatusItem, i);
    }

}