package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class NSDocTile extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSDocTile");
    static Pointer setBadgeLabel = Runtime.INSTANCE.sel_getUid("setBadgeLabel:");

    public NSDocTile(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public NSDocTile(long l) {
        super(l);
    }

    public void setBadgeLabel(NSString label) {
        Runtime.INSTANCE.objc_msgSend(this, setBadgeLabel, label);
    }

}