package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSApplicationDelegate extends NSObject {

    public static Pointer klass = Runtime.INSTANCE.objc_getProtocol("NSApplicationDelegate");

    public NSApplicationDelegate(long l) {
        super(l);
    }

    public NSApplicationDelegate(Pointer p) {
        super(Pointer.nativeValue(p));
    }

}