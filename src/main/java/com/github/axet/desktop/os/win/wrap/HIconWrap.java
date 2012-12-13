package com.github.axet.desktop.os.win.wrap;

import com.github.axet.desktop.os.win.GetLastErrorException;
import com.github.axet.desktop.os.win.handle.ICONINFO;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HICON;

public class HIconWrap extends HICON {

    // http://www.pinvoke.net/default.aspx/user32.createiconindirect

    static HICON createIconIndirect(HBITMAP bm) {
        ICONINFO info = new ICONINFO();
        info.IsIcon = true;
        info.MaskBitmap = bm;
        info.ColorBitmap = bm;

        HICON hicon = User32Ex.INSTANCE.CreateIconIndirect(info);
        if (hicon == null)
            throw new GetLastErrorException();

        return hicon;
    }

    public HIconWrap() {
    }

    public HIconWrap(Pointer p) {
        super(p);
    }

    public HIconWrap(HBitmapWrap bm) {
        setPointer(createIconIndirect(bm).getPointer());
    }

    protected void finalize() throws Throwable {
        close();

        super.finalize();
    }

    public void close() {
        if (Pointer.nativeValue(getPointer()) != 0) {
            GDI32.INSTANCE.DeleteObject(this);
            setPointer(new Pointer(0));
        }
    }
}
