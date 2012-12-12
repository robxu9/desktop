package com.github.axet.desktop.os.win;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Kernel32;

public class HResultException extends RuntimeException {

    private static final long serialVersionUID = 1120052658898156359L;

    String str;

    public HResultException(int hresult) {
        Memory m = new Memory(1024);
        Kernel32.INSTANCE.FormatMessage(Kernel32.FORMAT_MESSAGE_FROM_SYSTEM, null, hresult, 0, m, (int) m.size(), null);
        String mm = m.getString(0, true);
        mm = mm.trim();
        str = String.format("HRESULT: 0x%08x [%s]", hresult, mm);
    }

    public String toString() {
        return str;
    }
}
