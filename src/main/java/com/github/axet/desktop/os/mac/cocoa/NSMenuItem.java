package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class NSMenuItem extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSMenuItem");
    static Pointer setTitle = Runtime.INSTANCE.sel_getUid("setTitle:");
    static Pointer setImage = Runtime.INSTANCE.sel_getUid("setImage:");
    static Pointer setEnabled = Runtime.INSTANCE.sel_getUid("setEnabled:");
    static Pointer separatorItem = Runtime.INSTANCE.sel_getUid("separatorItem");
    static Pointer setSubmenu = Runtime.INSTANCE.sel_getUid("setSubmenu:");
    static Pointer setState = Runtime.INSTANCE.sel_getUid("setState:");
    static Pointer setTarget = Runtime.INSTANCE.sel_getUid("setTarget:");
    static Pointer setAction = Runtime.INSTANCE.sel_getUid("setAction:");

    public NSMenuItem() {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));
    }

    public NSMenuItem(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public NSMenuItem(long l) {
        super(l);
    }

    public static NSMenuItem separatorItem() {
        return new NSMenuItem(Runtime.INSTANCE.objc_msgSend(klass, separatorItem));
    }

    public void setTitle(NSString i) {
        Runtime.INSTANCE.objc_msgSend(this, setTitle, i);
    }

    public void setImage(NSImage i) {
        Runtime.INSTANCE.objc_msgSend(this, setImage, i);
    }

    public void setEnabled(boolean i) {
        Runtime.INSTANCE.objc_msgSend(this, setEnabled, i);
    }

    public void setSubmenu(NSMenu i) {
        Runtime.INSTANCE.objc_msgSend(this, setSubmenu, i);
    }

    public void setState(int i) {
        Runtime.INSTANCE.objc_msgSend(this, setState, i);
    }

    public void setTarget(NSObject o) {
        Runtime.INSTANCE.objc_msgSend(this, setTarget, o);
    }

    public void setAction(Pointer p) {
        Runtime.INSTANCE.objc_msgSend(this, setAction, p);
    }
}