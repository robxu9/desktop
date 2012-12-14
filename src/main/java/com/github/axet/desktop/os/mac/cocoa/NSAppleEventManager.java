package com.github.axet.desktop.os.mac.cocoa;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/Foundation/Classes/NSAppleEventManager_Class/Reference/Reference.html

public class NSAppleEventManager extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSAppleEventManager");
    static Pointer sharedAppleEventManager = Runtime.INSTANCE.sel_getUid("sharedAppleEventManager");
    static Pointer setEventHandlerAndSelectorForEventClassAndEventID = Runtime.INSTANCE
            .sel_getUid("setEventHandler:andSelector:forEventClass:andEventID:");
    static Pointer removeEventHandlerForEventClassAndEventID = Runtime.INSTANCE
            .sel_getUid("removeEventHandlerForEventClass:andEventID:");

    public static NSAppleEventManager sharedAppleEventManager() {
        return new NSAppleEventManager(Runtime.INSTANCE.objc_msgSend(klass, sharedAppleEventManager));
    }

    public NSAppleEventManager(long l) {
        super(l);
    }

    public NSAppleEventManager(Pointer p) {
        super(Pointer.nativeValue(p));
    }

    public void setEventHandlerAndSelectorForEventClassAndEventID(Pointer target, Pointer selector, long eventClass,
            long eventId) {
        Runtime.INSTANCE.objc_msgSend(this, setEventHandlerAndSelectorForEventClassAndEventID, target, selector,
                eventClass, eventId);
    }

    public void removeEventHandlerForEventClassAndEventID(long eventClass, long eventID) {
        Runtime.INSTANCE.objc_msgSend(this, removeEventHandlerForEventClassAndEventID, eventClass, eventID);
    }
}