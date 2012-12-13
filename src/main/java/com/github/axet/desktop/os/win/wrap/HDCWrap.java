package com.github.axet.desktop.os.win.wrap;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HDC;

public class HDCWrap extends HDC {

    public HDCWrap() {
    }

    public HDCWrap(Pointer p) {
        super(p);
    }

}
