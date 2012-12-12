package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSData extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSData");

    static Pointer dataWithBytesLength = Runtime.INSTANCE.sel_getUid("dataWithBytes:length:");

    public NSData(byte[] data) {
        super(Runtime.INSTANCE.objc_msgSend(klass, dataWithBytesLength, data, data.length));

        retain();
    }

    public NSData(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
    }

}