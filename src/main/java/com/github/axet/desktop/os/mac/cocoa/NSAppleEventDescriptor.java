package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSAppleEventDescriptor_Class/Reference/Reference.html

public class NSAppleEventDescriptor extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSAppleEventDescriptor");
    static Pointer numberOfItems = Runtime.INSTANCE.sel_getUid("numberOfItems");
    static Pointer descriptorAtIndex = Runtime.INSTANCE.sel_getUid("descriptorAtIndex:");
    static Pointer stringValue = Runtime.INSTANCE.sel_getUid("stringValue");

    public NSAppleEventDescriptor(long l) {
        super(l);
    }

    public NSAppleEventDescriptor(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public long numberOfItems() {
        return Runtime.INSTANCE.objc_msgSend(this, numberOfItems);
    }

    public NSString stringValue() {
        return new NSString(Runtime.INSTANCE.objc_msgSend(this, stringValue));
    }

    public NSAppleEventDescriptor descriptorAtIndex(int i) {
        return new NSAppleEventDescriptor(Runtime.INSTANCE.objc_msgSend(this, descriptorAtIndex, i));
    }
}