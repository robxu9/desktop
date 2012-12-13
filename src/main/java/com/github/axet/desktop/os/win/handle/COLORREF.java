package com.github.axet.desktop.os.win.handle;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class COLORREF extends HANDLE {
    public COLORREF() {

    }

    public COLORREF(Pointer p) {
        super(p);
    }
}
