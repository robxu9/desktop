package com.github.axet.desktop.os.win.handle;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class LOGFONT extends HANDLE {
    public LOGFONT() {

    }

    public LOGFONT(Pointer p) {
        super(p);
    }
}
