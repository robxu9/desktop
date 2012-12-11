package com.github.axet.desktop.os.mac.foundation;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

// thanks to https://gist.github.com/3974488

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ObjCRuntimeRef/Reference/reference.html

public interface Runtime extends Library {

    Runtime INSTANCE = (Runtime) Native.loadLibrary("objc", Runtime.class);

    public Pointer objc_lookUpClass(String name);

    String class_getName(Pointer cls);

    Pointer sel_getUid(String str);

    String sel_getName(Pointer aSelector);

    long objc_msgSend(Pointer theReceiver, Pointer theSelector, Object... string);

    long class_createInstance(Pointer cls, int extraBytes);
}
