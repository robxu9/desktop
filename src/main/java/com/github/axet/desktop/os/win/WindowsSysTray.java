package com.github.axet.desktop.os.win;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.axet.desktop.DesktopSysTray;
import com.github.axet.desktop.os.win.handle.ATOM;
import com.github.axet.desktop.os.win.handle.ICONINFO;
import com.github.axet.desktop.os.win.handle.NOTIFYICONDATA;
import com.github.axet.desktop.os.win.handle.WNDCLASSEX;
import com.github.axet.desktop.os.win.handle.WNDPROC;
import com.github.axet.desktop.os.win.libs.Shell32Ex;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.ptr.PointerByReference;

// http://www.nevaobject.com/_docs/_coroutine/coroutine.htm

public class WindowsSysTray extends DesktopSysTray {

    public static final int WM_TASKBARCREATED = User32Ex.INSTANCE.RegisterWindowMessage("TaskbarCreated");
    public static final int WM_LBUTTONDOWN = 513;
    public static final int WM_NCCREATE = 129;
    public static final int WM_NCCALCSIZE = 131;
    public static final int WM_CREATE = 1;
    public static final int WM_SIZE = 5;
    public static final int WM_MOVE = 3;
    public static final int WM_USER = 1024;
    public static final int WM_LBUTTONUP = 0x0202;
    public static final int WM_LBUTTONDBLCLK = 515;
    public static final int WM_RBUTTONUP = 517;
    public static final int WM_QUIT = 0x0012;
    public static final int WM_CLOSE = 0x0010;
    public static final int WM_NULL = 0x0000;
    public static final int SW_SHOW = 5;
    public static final int WM_COMMAND = 0x0111;
    public static final int WM_SHELLNOTIFY = WM_USER + 1;

    public static final int MF_ENABLED = 0;
    public static final int MF_DISABLED = 0x00000002;
    public static final int MF_CHECKED = 0x00000008;
    public static final int MF_UNCHECKED = 0;
    public static final int MF_GRAYED = 0x00000001;
    public static final int MF_STRING = 0x00000000;
    public static final int MF_SEPARATOR = 0x00000800;
    public static final int MF_POPUP = 0x00000010;

    public static final int TPM_RIGHTBUTTON = 0x0002;

    public static final int SM_CYMENUCHECK = 72;

    public class MessagePump implements Runnable {
        Thread t;

        final String klass = "SystemTrayIcon";
        ATOM wcatom;
        WNDPROC WndProc;
        HWND hWnd;
        HINSTANCE hInstance;

        Object lock = new Object();

        public MessagePump() {
            t = new Thread(this, "MessagePump");
        }

        public void start() {
            synchronized (lock) {
                t.start();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        void create() {
            WndProc = new WNDPROC() {
                public LRESULT callback(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam) {
                    switch (msg) {
                    case WM_SHELLNOTIFY:
                        switch (lParam.intValue()) {
                        case WM_LBUTTONUP:
                            for (Listener l : Collections.synchronizedCollection(listeners)) {
                                l.mouseLeftClick();
                            }
                            break;
                        case WM_LBUTTONDBLCLK:
                            for (Listener l : Collections.synchronizedCollection(listeners)) {
                                l.mouseLeftDoubleClick();
                            }
                            break;
                        case WM_RBUTTONUP:
                            for (Listener l : Collections.synchronizedCollection(listeners)) {
                                l.mouseRightClick();
                            }
                            break;
                        }
                        break;
                    case WM_COMMAND: {
                        int nID = wParam.intValue() & 0xff;
                        MenuMap m = hmenusids.get(nID);
                        m.fire();
                        break;
                    }
                    case WM_QUIT:
                        User32.INSTANCE.PostQuitMessage(0);
                        break;
                    }

                    if (msg == WM_TASKBARCREATED) {
                        show();
                    }

                    return User32Ex.INSTANCE.DefWindowProc(hWnd, msg, wParam, lParam);
                }
            };
            hWnd = createWindow();
        }

        // http://osdir.com/ml/java.jna.user/2008-07/msg00049.html

        HWND createWindow() {
            hInstance = Kernel32.INSTANCE.GetModuleHandle(null);

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

            HWND hwnd = User32Ex.INSTANCE.CreateWindowEx(0, klass, klass, User32Ex.WS_OVERLAPPEDWINDOW, 0, 0, 0, 0,
                    null, null, hInstance, null);

            if (hwnd == null)
                throw new GetLastErrorException();

            return hwnd;
        }

        @Override
        public void run() {
            create();

            synchronized (lock) {
                lock.notifyAll();
            }

            MSG msg = new MSG();

            while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) > 0) {
                User32.INSTANCE.DispatchMessage(msg);
            }

            destory();
        }

        void destory() {
            if (hWnd != null) {
                if (!User32Ex.INSTANCE.DestroyWindow(hWnd))
                    throw new GetLastErrorException();
                hWnd = null;
            }

            if (wcatom != null) {
                if (!User32Ex.INSTANCE.UnregisterClass(klass, hInstance))
                    throw new GetLastErrorException();
                wcatom = null;
            }
        }

        void close() {
            User32Ex.INSTANCE.SendMessage(hWnd, WM_QUIT, null, null);

            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static class MenuMap {
        public HBITMAP hbm;
        public JMenuItem item;

        public MenuMap(JMenuItem item, HBITMAP hbm) {
            this.hbm = hbm;
            this.item = item;
        }

        public void fire() {
            item.doClick();
        }
    }

    static int count = 0;
    static MessagePump mp;

    boolean close = false;

    Icon icon;
    String title;
    JPopupMenu menu;

    HBITMAP hbm;
    HICON hico;
    List<HMENU> hmenus = new ArrayList<HMENU>();
    // position in this list == id of HMENU item
    List<MenuMap> hmenusids = new ArrayList<MenuMap>();

    public WindowsSysTray() {
        create();
    }

    void create() {
        if (count == 0) {
            mp = new MessagePump();
            mp.start();
        }
        count++;
    }

    public void close() {
        if (hbm != null) {
            GDI32.INSTANCE.DeleteObject(hbm);
            hbm = null;
        }
        if (hico != null) {
            GDI32.INSTANCE.DeleteObject(hico);
            hico = null;
        }
        clearMenus();

        if (!close) {
            close = true;

            count--;
            if (count == 0) {
                if (mp != null) {
                    mp.close();
                    mp = null;
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();

        close();
    }

    BufferedImage createBm(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    HBITMAP createBitmap(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return createBitmap(bi);
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

    HBITMAP getMenuImage(Icon icon) {
        BufferedImage img = createBm(icon);

        int menubarHeigh = User32.INSTANCE.GetSystemMetrics(SM_CYMENUCHECK);

        BufferedImage scaledImage = new BufferedImage(menubarHeigh, menubarHeigh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(img, 0, 0, menubarHeigh, menubarHeigh, null);
        graphics2D.dispose();

        return createBitmap(scaledImage);
    }

    // http://www.pinvoke.net/default.aspx/user32.createiconindirect

    HICON createIconIndirect(HBITMAP bm) {
        ICONINFO info = new ICONINFO();
        info.IsIcon = true;
        info.MaskBitmap = bm;
        info.ColorBitmap = bm;

        HICON hicon = User32Ex.INSTANCE.CreateIconIndirect(info);
        if (hicon == null)
            throw new GetLastErrorException();

        return hicon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setTitle(String t) {
        title = t;
    }

    void clearMenus() {
        for (HMENU hmenu : hmenus) {
            User32Ex.INSTANCE.DestroyMenu(hmenu);
        }
        hmenus.clear();
        for (MenuMap m : hmenusids) {
            GDI32.INSTANCE.DeleteObject(m.hbm);
        }
        hmenusids.clear();
    }

    public void setMenu(JPopupMenu menu) {
        this.menu = menu;
    }

    void updateMenus() {
        clearMenus();

        HMENU hmenu = User32Ex.INSTANCE.CreatePopupMenu();
        hmenus.add(hmenu);

        for (int i = 0; i < menu.getComponentCount(); i++) {
            Component e = menu.getComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                HMENU hsub = createSubmenu(sub);

                HBITMAP bm = null;
                if (sub.getIcon() != null)
                    bm = getMenuImage(sub.getIcon());
                hmenusids.add(new MenuMap(sub, bm));

                // you know, the usual windows tricks (transfer handle to
                // decimal)
                int handle = (int) Pointer.nativeValue(hsub.getPointer());

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MF_POPUP, handle, sub.getText()))
                    throw new GetLastErrorException();
                if (!User32Ex.INSTANCE.SetMenuItemBitmaps(hmenu, handle, 0, bm, bm))
                    throw new GetLastErrorException();

                hmenus.add(hsub);
            } else if (e instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                int nID = hmenusids.size();
                HBITMAP bm = null;
                if (ch.getIcon() != null)
                    bm = getMenuImage(ch.getIcon());
                hmenusids.add(new MenuMap(ch, bm));

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, (ch.getState() ? MF_CHECKED : MF_UNCHECKED)
                        | (ch.isEnabled() ? MF_ENABLED : MF_GRAYED) | MF_STRING, nID, ch.getText()))
                    throw new GetLastErrorException();
                // it will replace checkbox with an image
                //
                // if (!User32Ex.INSTANCE.SetMenuItemBitmaps(hmenu, nID, 0, bm,
                // bm))
                // throw new GetLastErrorException();
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                int nID = hmenusids.size();
                HBITMAP bm = null;
                if (mi.getIcon() != null)
                    bm = getMenuImage(mi.getIcon());
                hmenusids.add(new MenuMap(mi, bm));

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, (mi.isEnabled() ? MF_ENABLED : MF_GRAYED) | MF_STRING, nID,
                        mi.getText()))
                    throw new GetLastErrorException();
                if (!User32Ex.INSTANCE.SetMenuItemBitmaps(hmenu, nID, 0, bm, bm))
                    throw new GetLastErrorException();
            }

            if (e instanceof JPopupMenu.Separator) {
                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MF_SEPARATOR, 0, null))
                    throw new GetLastErrorException();
            }
        }
    }

    HMENU createSubmenu(JMenu menu) {
        HMENU hsub = User32Ex.INSTANCE.CreatePopupMenu();

        for (int i = 0; i < menu.getMenuComponentCount(); i++) {
            Component e = menu.getMenuComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                HMENU hsub2 = createSubmenu(sub);

                // you know, the usual windows tricks (transfer handle to
                // decimal)
                int handle = (int) Pointer.nativeValue(hsub2.getPointer());

                HBITMAP bm = null;
                if (sub.getIcon() != null)
                    bm = getMenuImage(sub.getIcon());
                hmenusids.add(new MenuMap(sub, bm));

                if (!User32Ex.INSTANCE.AppendMenu(hsub, MF_POPUP, handle, sub.getText()))
                    throw new GetLastErrorException();
                if (!User32Ex.INSTANCE.SetMenuItemBitmaps(hsub, handle, 0, bm, bm))
                    throw new GetLastErrorException();

                hmenus.add(hsub2);
            } else if (e instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                int nID = hmenusids.size();
                HBITMAP bm = null;
                if (ch.getIcon() != null)
                    bm = getMenuImage(ch.getIcon());
                hmenusids.add(new MenuMap(ch, bm));

                if (!User32Ex.INSTANCE.AppendMenu(hsub, (ch.getState() ? MF_CHECKED : MF_UNCHECKED)
                        | (ch.isEnabled() ? MF_ENABLED : MF_GRAYED) | MF_STRING, nID, ch.getText()))
                    throw new GetLastErrorException();
                // it will replace checkbox with an image
                //
                // if (!User32Ex.INSTANCE.SetMenuItemBitmaps(hsub, nID, 0, bm,
                // bm))
                // throw new GetLastErrorException();
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                int nID = hmenusids.size();
                HBITMAP bm = null;
                if (mi.getIcon() != null)
                    bm = getMenuImage(mi.getIcon());
                hmenusids.add(new MenuMap(mi, bm));

                if (!User32Ex.INSTANCE.AppendMenu(hsub, (mi.isEnabled() ? MF_ENABLED : MF_GRAYED) | MF_STRING, nID,
                        mi.getText()))
                    throw new GetLastErrorException();
                if (!User32Ex.INSTANCE.SetMenuItemBitmaps(hsub, nID, 0, bm, bm))
                    throw new GetLastErrorException();
            }

            if (e instanceof JPopupMenu.Separator) {
                if (!User32Ex.INSTANCE.AppendMenu(hsub, MF_SEPARATOR, 0, null))
                    throw new GetLastErrorException();
            }
        }

        return hsub;

    }

    public void show() {
        if (hbm != null) {
            GDI32.INSTANCE.DeleteObject(hbm);
            hbm = null;
        }
        if (hico != null) {
            GDI32.INSTANCE.DeleteObject(hico);
            hico = null;
        }

        hbm = createBitmap(icon);
        hico = createIconIndirect(hbm);

        NOTIFYICONDATA nid = new NOTIFYICONDATA();
        nid.setTooltip(title);
        nid.hWnd = mp.hWnd;
        nid.uCallbackMessage = WM_SHELLNOTIFY;
        nid.hIcon = hico;

        if (!Shell32Ex.INSTANCE.Shell_NotifyIcon(Shell32Ex.NIM_ADD, nid))
            throw new GetLastErrorException();
    }

    public void update() {
        if (hbm != null) {
            GDI32.INSTANCE.DeleteObject(hbm);
            hbm = null;
        }
        if (hico != null) {
            GDI32.INSTANCE.DeleteObject(hico);
            hico = null;
        }

        hbm = createBitmap(icon);
        hico = createIconIndirect(hbm);

        NOTIFYICONDATA nid = new NOTIFYICONDATA();
        nid.setTooltip(title);
        nid.hWnd = mp.hWnd;
        nid.uCallbackMessage = WM_SHELLNOTIFY;
        nid.hIcon = hico;

        if (!Shell32Ex.INSTANCE.Shell_NotifyIcon(Shell32Ex.NIM_MODIFY, nid))
            throw new GetLastErrorException();
    }

    public void hide() {
        NOTIFYICONDATA nid = new NOTIFYICONDATA();
        nid.hWnd = mp.hWnd;

        if (!Shell32Ex.INSTANCE.Shell_NotifyIcon(Shell32Ex.NIM_DELETE, nid))
            throw new GetLastErrorException();
    }

    public void showContextMenu() {
        updateMenus();
        User32.INSTANCE.SetForegroundWindow(mp.hWnd);

        Point p = MouseInfo.getPointerInfo().getLocation();
        if (!User32Ex.INSTANCE.TrackPopupMenu(hmenus.get(0), TPM_RIGHTBUTTON, p.x, p.y, 0, mp.hWnd, null))
            throw new GetLastErrorException();

        User32.INSTANCE.PostMessage(mp.hWnd, WM_NULL, null, null);
    }
}
