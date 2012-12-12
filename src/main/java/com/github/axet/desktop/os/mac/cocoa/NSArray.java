package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSArray extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSArray");
    static Pointer count = Runtime.INSTANCE.sel_getUid("count");
    static Pointer objectAtIndex = Runtime.INSTANCE.sel_getUid("objectAtIndex:");

    public NSArray(long l) {
        super(l);
    }

    public NSArray(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public long count() {
        return Runtime.INSTANCE.objc_msgSend(this, count);
    }

    public NSObject objectAtIndex(int i) {
        return new NSObject(Runtime.INSTANCE.objc_msgSend(this, objectAtIndex, i));
    }
}