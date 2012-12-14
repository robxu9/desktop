package com.github.axet.desktop.os.mac;

import java.util.HashMap;

import javax.swing.JMenuItem;

import com.github.axet.desktop.os.mac.cocoa.NSObject;
import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public class OSXSysTrayAction extends NSObject {

    //
    // register
    //

    final static Pointer registerKlass = Runtime.INSTANCE.objc_allocateClassPair(NSObject.klass,
            OSXSysTrayAction.class.getSimpleName(), 0);

    final static Pointer registerActionSelector = Runtime.INSTANCE.sel_registerName("action");

    final static Action registerAction = new Action() {
        public void callback(Pointer self, Pointer selector) {
            if (selector.equals(registerActionSelector)) {
                OSXSysTrayAction a = map.get(Pointer.nativeValue(self));
                a.mi.doClick();
            }
        }
    };

    public interface Action extends StdCallCallback {
        public void callback(Pointer self, Pointer selector);
    }

    static {
        if (!Runtime.INSTANCE.class_addMethod(registerKlass, registerActionSelector, registerAction, "v@:"))
            throw new RuntimeException("problem initalizing class");

        Runtime.INSTANCE.objc_registerClassPair(registerKlass);
    }

    //
    // class instances
    //

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass(OSXSysTrayAction.class.getSimpleName());

    static Pointer action = Runtime.INSTANCE.sel_getUid("action");

    //
    // members
    //

    static HashMap<Long, OSXSysTrayAction> map = new HashMap<Long, OSXSysTrayAction>();

    JMenuItem mi;

    public OSXSysTrayAction(JMenuItem mi) {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        map.put(Pointer.nativeValue(this), this);

        this.mi = mi;
    }

    protected void finalize() throws Throwable {
        map.remove(Pointer.nativeValue(this));

        super.finalize();
    }

}