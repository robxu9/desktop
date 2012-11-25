package com.github.axet.desktop.os.win;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface Ole32 extends Library {

    static Ole32 INSTANCE = (Ole32) Native.loadLibrary("Ole32", Ole32.class);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms680722(v=vs.85).aspx

    /**
     * void CoTaskMemFree( _In_opt_ LPVOID pv );
     */

    public void CoTaskMemFree(Pointer pv);

}
