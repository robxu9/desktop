package com.github.axet.desktop.os.win.wrap;

import com.github.axet.desktop.os.win.GetLastErrorException;
import com.github.axet.desktop.os.win.handle.ATOM;
import com.github.axet.desktop.os.win.handle.WNDCLASSEX;
import com.github.axet.desktop.os.win.handle.WNDPROC;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;

public class WndClassExWrap {

    String klass;
    ATOM wcatom;
    HINSTANCE hInstance;

    public WndClassExWrap(HINSTANCE hInstance, WNDPROC WndProc, String klass) {
        this.klass = klass;
        this.hInstance = hInstance;

        WNDCLASSEX wc = new WNDCLASSEX();
        wc.cbSize = wc.size();
        wc.style = 0;
        wc.lpfnWndProc = WndProc;
        wc.cbClsExtra = 0;
        wc.cbWndExtra = 0;
        wc.hInstance = hInstance;
        wc.hIcon = null;
        wc.hbrBackground = null;
        wc.lpszMenuName = null;
        wc.lpszClassName = new WString(klass);

        wcatom = User32Ex.INSTANCE.RegisterClassEx(wc);
        if (wcatom == null)
            throw new GetLastErrorException();
    }

    public void close() {
        if (wcatom != null) {
            if (!User32Ex.INSTANCE.UnregisterClass(klass, hInstance))
                throw new GetLastErrorException();
            wcatom = null;
        }
    }

    public String getName() {
        return klass;
    }

    protected void finalize() throws Throwable {
        close();

        super.finalize();
    }

}
