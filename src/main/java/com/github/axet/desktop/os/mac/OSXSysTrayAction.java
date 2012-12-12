package com.github.axet.desktop.os.mac;

import java.util.HashMap;

import javax.swing.JMenuItem;

import com.github.axet.desktop.os.mac.cocoa.NSObject;
import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class OSXSysTrayAction extends NSObject {

    public interface Action extends StdCallCallback {
        public void callback(Pointer self, Pointer selector);
    }

    static {
        final Pointer klass = Runtime.INSTANCE.objc_allocateClassPair(NSObject.klass,
                OSXSysTrayAction.class.getSimpleName(), 0);
        final Pointer action = Runtime.INSTANCE.sel_registerName("action");

        boolean add = Runtime.INSTANCE.class_addMethod(klass, action, new Action() {
            public void callback(Pointer self, Pointer selector) {
                if (selector.equals(action)) {
                    OSXSysTrayAction a = map.get(Pointer.nativeValue(self));
                    a.mi.doClick();
                }
            }
        }, "");

        if (!add)
            throw new RuntimeException("problem initalizing class");

        Runtime.INSTANCE.objc_registerClassPair(klass);
    }

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass(OSXSysTrayAction.class.getSimpleName());

    static Pointer action = Runtime.INSTANCE.sel_getUid("action");

    static HashMap<Long, OSXSysTrayAction> map = new HashMap<Long, OSXSysTrayAction>();

    JMenuItem mi;

    public OSXSysTrayAction(JMenuItem mi) {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        retain();

        map.put(Pointer.nativeValue(this), this);

        this.mi = mi;
    }

    protected void finalize() throws Throwable {
        map.remove(Pointer.nativeValue(this));

        release();
    }

}