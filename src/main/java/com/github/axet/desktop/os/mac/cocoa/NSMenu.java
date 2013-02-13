package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class NSMenu extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSMenu");
    static Pointer addItem = Runtime.INSTANCE.sel_getUid("addItem:");
    static Pointer setAutoenablesItems = Runtime.INSTANCE.sel_getUid("setAutoenablesItems:");
    static Pointer itemAtIndex = Runtime.INSTANCE.sel_getUid("itemAtIndex:");
    static Pointer insertItemAtIndex = Runtime.INSTANCE.sel_getUid("insertItem:atIndex:");

    public NSMenu() {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));
    }

    public NSMenu(long l) {
        super(l);
    }

    public NSMenu(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public void addItem(NSMenuItem i) {
        Runtime.INSTANCE.objc_msgSend(this, addItem, i);
    }

    public void insertItemAtIndex(NSMenuItem i, int in) {
        Runtime.INSTANCE.objc_msgSend(this, insertItemAtIndex, i, in);
    }

    public void setAutoenablesItems(boolean b) {
        Runtime.INSTANCE.objc_msgSend(this, setAutoenablesItems, b);
    }

    public NSMenuItem itemAtIndex(int i) {
        return new NSMenuItem(Runtime.INSTANCE.objc_msgSend(this, itemAtIndex, i));
    }
}