package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class NSMenu extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSMenu");
    static Pointer addItem = Runtime.INSTANCE.sel_getUid("addItem:");
    static Pointer setAutoenablesItems = Runtime.INSTANCE.sel_getUid("setAutoenablesItems:");

    public NSMenu() {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));
    }

    public NSMenu(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public void addItem(NSMenuItem i) {
        Runtime.INSTANCE.objc_msgSend(this, addItem, i);
    }

    public void setAutoenablesItems(boolean b) {
        Runtime.INSTANCE.objc_msgSend(this, setAutoenablesItems, b);
    }
}