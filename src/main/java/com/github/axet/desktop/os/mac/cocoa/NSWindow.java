package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// http://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSData_Class/Reference/Reference.html#//apple_ref/doc/c_ref/NSData

public class NSWindow extends NSObject {

    public static final int NSBackingStoreBuffered = 2;
    public static final int NSBorderlessWindowMask = 0;

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSWindow");
    static Pointer initWithContentRectStyleMaskBackingDefer = Runtime.INSTANCE
            .sel_getUid("initWithContentRect:styleMask:backing:defer:");
    static Pointer makeKeyAndOrderFront = Runtime.INSTANCE.sel_getUid("makeKeyAndOrderFront:");

    public NSWindow() {
        super(Runtime.INSTANCE.objc_msgSend(klass, alloc));
    }

    public NSWindow(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public NSWindow(long l) {
        super(l);
    }

    public static NSWindow initWithContentRectStyleMaskBackingDefer(NSRect.ByValue contentRect, int windowStyle,
            int bufferingType, boolean deferCreation) {
        Pointer a = new Pointer(Runtime.INSTANCE.objc_msgSend(klass, alloc));
        return new NSWindow(Runtime.INSTANCE.objc_msgSend(a, initWithContentRectStyleMaskBackingDefer, contentRect,
                windowStyle, bufferingType, deferCreation));
    }

    public void makeKeyAndOrderFront(Pointer sender) {
        Runtime.INSTANCE.objc_msgSend(this, makeKeyAndOrderFront, sender);
    }

}