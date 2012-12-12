package com.github.axet.desktop.os.win.libs;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface Ole32Ex extends Library {

    static Ole32Ex INSTANCE = (Ole32Ex) Native.loadLibrary("Ole32", Ole32Ex.class);

    // http://msdn.microsoft.com/en-us/library/windows/desktop/ms680722(v=vs.85).aspx

    /**
     * void CoTaskMemFree( _In_opt_ LPVOID pv );
     */

    public void CoTaskMemFree(Pointer pv);

}
