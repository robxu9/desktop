package com.github.axet.desktop.os.win.handle;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class ATOM extends HANDLE {
    public ATOM() {

    }

    public ATOM(Pointer p) {
        super(p);
    }
}
