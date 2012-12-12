package com.github.axet.desktop.os.win;

import com.sun.jna.platform.win32.Kernel32;

public class GetLastErrorException extends HResultException {

    private static final long serialVersionUID = 1120052658898156359L;

    public GetLastErrorException() {
        super(Kernel32.INSTANCE.GetLastError());
    }
}
