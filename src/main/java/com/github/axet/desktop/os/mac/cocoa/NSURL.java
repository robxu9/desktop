package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSURL extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSURL");
    static Pointer absoluteString = Runtime.INSTANCE.sel_getUid("absoluteString");
    static Pointer path = Runtime.INSTANCE.sel_getUid("path");

    public NSURL(Pointer p) {
        super(Pointer.nativeValue(p));

    }

    public NSString absoluteString() {
        return new NSString(Runtime.INSTANCE.objc_msgSend(this, absoluteString));
    }

    public NSString path() {
        return new NSString(Runtime.INSTANCE.objc_msgSend(this, path));
    }
}