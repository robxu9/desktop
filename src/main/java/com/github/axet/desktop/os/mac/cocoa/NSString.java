package com.github.axet.desktop.os.mac.cocoa;

import org.apache.commons.codec.binary.StringUtils;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// thanks so https://gist.github.com/3974488

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSString_Class/Reference/NSString.html

public class NSString extends NSObject {
    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSString");
    static Pointer stringWithUTF8String = Runtime.INSTANCE.sel_getUid("stringWithUTF8String:");
    static Pointer UTF8String = Runtime.INSTANCE.sel_getUid("UTF8String");

    public NSString(String str) {
        super(Runtime.INSTANCE.objc_msgSend(klass, stringWithUTF8String, StringUtils.getBytesUtf8(str + "\0")));
    }

    public NSString(long l) {
        super(l);
    }

    public NSString(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public String toString() {
        long outStringPtr = Runtime.INSTANCE.objc_msgSend(this, UTF8String);

        return new Pointer(outStringPtr).getString(0);
    }

}
