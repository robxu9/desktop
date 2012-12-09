package com.github.axet.apple;

import com.github.axet.apple.fundations.CoreFoundation;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;


public class CFArrayRef extends PointerByReference {

    public int getCount() {
        return CoreFoundation.INSTANCE.CFArrayGetCount(this);
    }

    public Pointer get(int i) {
        return CoreFoundation.INSTANCE.CFArrayGetValueAtIndex(this, i);
    }
}
