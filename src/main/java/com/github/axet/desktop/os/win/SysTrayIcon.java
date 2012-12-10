package com.github.axet.desktop.os.win;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import com.github.axet.desktop.os.win.handle.ATOM;
import com.github.axet.desktop.os.win.handle.ICONINFO;
import com.github.axet.desktop.os.win.handle.NOTIFYICONDATA;
import com.github.axet.desktop.os.win.handle.WNDCLASSEX;
import com.github.axet.desktop.os.win.handle.WNDPROC;
import com.github.axet.desktop.os.win.libs.Shell32;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.ptr.PointerByReference;

// http://www.nevaobject.com/_docs/_coroutine/coroutine.htm

public class SysTrayIcon {

    public static final int WM_TASKBARCREATED = User32Ex.INSTANCE.RegisterWindowMessage("TaskbarCreated");
    public static final int WM_LBUTTONDOWN = 513;

    public static interface Listener {
        public void mouseLeftClick();

        public void mouseRightClick();
    }

    public static class MessagePump implements Runnable {
        Thread t;

        public MessagePump() {
            t = new Thread(this, "MessagePump");
            t.start();
        }

        @Override
        public void run() {
            MSG msg = new MSG();

            while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) > 0) {
                User32.INSTANCE.DispatchMessage(msg);
            }
        }
    }

    static WNDPROC WndProc;
    static HWND hWnd;
    static MessagePump mp;

    static {
        WndProc = new WNDPROC() {
            public LRESULT callback(HWND hWnd, int uMsg, WPARAM wParam, LPARAM lParam) {
                switch (uMsg) {
                case WM_LBUTTONDOWN:
                    break;
                }

                if (uMsg == WM_TASKBARCREATED) {
                    ;
                }
                return User32Ex.INSTANCE.DefWindowProc(hWnd, uMsg, wParam, lParam);
            }
        };

        hWnd = createWindow();

        mp = new MessagePump();
    }

    public SysTrayIcon() {
    }

    // https://github.com/twall/jna/blob/master/contrib/alphamaskdemo/com/sun/jna/contrib/demo/AlphaMaskDemo.java

    HBITMAP createBitmap(BufferedImage image) {
        User32 user = User32.INSTANCE;
        GDI32 gdi = GDI32.INSTANCE;

        int w = image.getWidth(null);
        int h = image.getHeight(null);
        HDC screenDC = user.GetDC(null);
        HDC memDC = gdi.CreateCompatibleDC(screenDC);
        HBITMAP hBitmap = null;

        try {
            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics g = buf.getGraphics();
            g.drawImage(image, 0, 0, w, h, null);

            BITMAPINFO bmi = new BITMAPINFO();
            bmi.bmiHeader.biWidth = w;
            bmi.bmiHeader.biHeight = h;
            bmi.bmiHeader.biPlanes = 1;
            bmi.bmiHeader.biBitCount = 32;
            bmi.bmiHeader.biCompression = WinGDI.BI_RGB;
            bmi.bmiHeader.biSizeImage = w * h * 4;

            PointerByReference ppbits = new PointerByReference();
            hBitmap = gdi.CreateDIBSection(memDC, bmi, WinGDI.DIB_RGB_COLORS, ppbits, null, 0);
            Pointer pbits = ppbits.getValue();

            Raster raster = buf.getData();
            int[] pixel = new int[4];
            int[] bits = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    raster.getPixel(x, h - y - 1, pixel);
                    int alpha = (pixel[3] & 0xFF) << 24;
                    int red = (pixel[2] & 0xFF);
                    int green = (pixel[1] & 0xFF) << 8;
                    int blue = (pixel[0] & 0xFF) << 16;
                    bits[x + y * w] = alpha | red | green | blue;
                }
            }
            pbits.write(0, bits, 0, bits.length);
            return hBitmap;
        } finally {
            user.ReleaseDC(null, screenDC);
            gdi.DeleteDC(memDC);
        }
    }

    // http://www.pinvoke.net/default.aspx/user32.createiconindirect

    HICON createIconIndirect(BufferedImage img) {
        HBITMAP bm = createBitmap(img);

        ICONINFO info = new ICONINFO();
        info.IsIcon = true;
        info.MaskBitmap = bm;
        info.ColorBitmap = bm;

        HICON hicon = User32Ex.INSTANCE.CreateIconIndirect(info);
        if (hicon == null)
            throw new GetLastErrorException();

        return hicon;
    }

    // http://osdir.com/ml/java.jna.user/2008-07/msg00049.html

    static HWND createWindow() {
        WNDCLASSEX wc = new WNDCLASSEX();
        wc.cbSize = wc.size();
        wc.style = 0;
        wc.lpfnWndProc = WndProc;
        wc.cbClsExtra = 0;
        wc.cbWndExtra = 0;
        wc.hInstance = Kernel32.INSTANCE.GetModuleHandle(null);
        wc.hIcon = null;
        wc.hbrBackground = null;
        wc.lpszMenuName = null;
        wc.lpszClassName = "SystemTrayIcon";

        ATOM atom = User32Ex.INSTANCE.RegisterClassEx(wc);
        if (atom == null)
            throw new GetLastErrorException();

        HWND hwnd = User32Ex.INSTANCE.CreateWindowEx(0, wc.lpszClassName, null, User32.WS_POPUP, 0, 0, 0, 0, null,
                null, wc.hInstance, null);

        if (hwnd == null)
            throw new GetLastErrorException();

        return hwnd;
    }

    public void addIcon(BufferedImage icon) {
        NOTIFYICONDATA data = new NOTIFYICONDATA();
        data.setTooltip("Java Tool");
        data.hWnd = hWnd;
        data.uCallbackMessage = 1;
        data.hIcon = createIconIndirect(icon);

        boolean res = Shell32.INSTANCE.Shell_NotifyIcon(Shell32.NIM_ADD, data);
        if (!res)
            throw new GetLastErrorException();
    }
}
