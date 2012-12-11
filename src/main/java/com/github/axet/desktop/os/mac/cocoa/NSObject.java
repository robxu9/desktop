package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSObject extends Pointer {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSObject");

    static Pointer retain = Runtime.INSTANCE.sel_getUid("retain");
    static Pointer release = Runtime.INSTANCE.sel_getUid("release");

    public NSObject(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public NSObject(long l) {
        super(l);
    }

    public void retain() {
        Runtime.INSTANCE.objc_msgSend(this, retain);
    }

    public void release() {
        Runtime.INSTANCE.objc_msgSend(this, release);
    }

}