package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSFont extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSFont");

    static Pointer menuFontOfSize = Runtime.INSTANCE.sel_getUid("menuFontOfSize:");

    static Pointer menuBarFontOfSize = Runtime.INSTANCE.sel_getUid("menuBarFontOfSize:");

    static Pointer pointSize = Runtime.INSTANCE.sel_getUid("pointSize");

    public final static int NSCriticalRequest = 0;
    public final static int NSInformationalRequest = 10;

    public static NSFont menuFontOfSize(double size) {
        return new NSFont(Runtime.INSTANCE.objc_msgSend(klass, menuFontOfSize));
    }

    public static NSFont menuBarFontOfSize(double size) {
        return new NSFont(Runtime.INSTANCE.objc_msgSend(klass, menuBarFontOfSize));
    }

    public NSFont(long l) {
        super(l);

        retain();
    }

    public NSFont(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
    }

    public long pointSize() {
        return Runtime.INSTANCE.objc_msgSend(this, pointSize);
    }

}