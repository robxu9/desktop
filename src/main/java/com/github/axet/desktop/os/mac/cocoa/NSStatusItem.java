package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSStatusItem extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSStatusItem");

    static Pointer setHighlightMode = Runtime.INSTANCE.sel_getUid("setHighlightMode:");

    static Pointer setImage = Runtime.INSTANCE.sel_getUid("setImage:");

    static Pointer setTitle = Runtime.INSTANCE.sel_getUid("setTitle:");

    static Pointer setMenu = Runtime.INSTANCE.sel_getUid("setMenu:");

    public NSStatusItem(long l) {
        super(l);

        retain();
    }

    public NSStatusItem(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
    }

    public void setHighlightMode(boolean b) {
        Runtime.INSTANCE.objc_msgSend(this, setHighlightMode, b);
    }

    public void setImage(NSImage i) {
        Runtime.INSTANCE.objc_msgSend(this, setImage, i);
    }

    public void setTitle(NSString t) {
        Runtime.INSTANCE.objc_msgSend(this, setTitle, t);
    }

    public void setMenu(NSMenu t) {
        Runtime.INSTANCE.objc_msgSend(this, setMenu, t);
    }
}