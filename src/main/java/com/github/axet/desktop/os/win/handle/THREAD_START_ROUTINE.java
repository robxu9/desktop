package com.github.axet.desktop.os.win.handle;

import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public interface THREAD_START_ROUTINE extends StdCallCallback {
    long callback(long lpParameter);
}
