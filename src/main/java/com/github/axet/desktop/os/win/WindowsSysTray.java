package com.github.axet.desktop.os.win;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.axet.desktop.DesktopSysTray;
import com.github.axet.desktop.os.win.handle.ATOM;
import com.github.axet.desktop.os.win.handle.DRAWITEMSTRUCT;
import com.github.axet.desktop.os.win.handle.ICONINFO;
import com.github.axet.desktop.os.win.handle.LOGFONT;
import com.github.axet.desktop.os.win.handle.MEASUREITEMSTRUCT;
import com.github.axet.desktop.os.win.handle.MENUITEMINFO;
import com.github.axet.desktop.os.win.handle.NONCLIENTMETRICS;
import com.github.axet.desktop.os.win.handle.NOTIFYICONDATA;
import com.github.axet.desktop.os.win.handle.WNDCLASSEX;
import com.github.axet.desktop.os.win.handle.WNDPROC;
import com.github.axet.desktop.os.win.libs.GDI32Ex;
import com.github.axet.desktop.os.win.libs.Shell32Ex;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HFONT;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.SIZE;
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
    public static final int WM_MEASUREITEM = 44;
    public static final int WM_DRAWITEM = 43;

    public static final int MF_ENABLED = 0;
    public static final int MF_DISABLED = 0x00000002;
    public static final int MF_CHECKED = 0x00000008;
    public static final int MF_UNCHECKED = 0;
    public static final int MF_GRAYED = 0x00000001;
    public static final int MF_STRING = 0x00000000;
    public static final int MFT_OWNERDRAW = 256;
    public static final int MF_SEPARATOR = 0x00000800;
    public static final int MF_POPUP = 0x00000010;

    public static final int TPM_RIGHTBUTTON = 0x0002;

    public static final int SM_CYMENUCHECK = 72;

    static final int SPACE_ICONS = 2;

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
                        MenuMap m = hMenusIDs.get(nID);
                        m.fire();
                        break;
                    }
                    case WM_MEASUREITEM: {
                        MEASUREITEMSTRUCT ms = new MEASUREITEMSTRUCT(new Pointer(lParam.longValue()));

                        MenuMap mm = hMenusIDs.get(ms.itemData.intValue());
                        HDC hdc = User32.INSTANCE.GetDC(hWnd);
                        HFONT hfntOld = (HFONT) GDI32.INSTANCE.SelectObject(hdc, getSystemMenuFont());
                        SIZE size = new SIZE();
                        if (!GDI32Ex.INSTANCE.GetTextExtentPoint32(hdc, mm.item.getText(), mm.item.getText().length(),
                                size))
                            throw new GetLastErrorException();
                        GDI32.INSTANCE.SelectObject(hdc, hfntOld);
                        User32.INSTANCE.ReleaseDC(hWnd, hdc);

                        ms.itemWidth = (getSystemMenuImageSize() + SPACE_ICONS) * 2 + size.cx;
                        ms.itemHeight = size.cy;
                        ms.write();
                        break;
                    }
                    case WM_DRAWITEM: {
                        DRAWITEMSTRUCT di = new DRAWITEMSTRUCT(new Pointer(lParam.longValue()));

                        MenuMap mm = hMenusIDs.get(di.itemData.intValue());

                        if ((di.itemState & DRAWITEMSTRUCT.ODS_SELECTED) == DRAWITEMSTRUCT.ODS_SELECTED) {
                            GDI32Ex.INSTANCE.SetTextColor(di.hDC,
                                    User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_HIGHLIGHTTEXT));
                            GDI32Ex.INSTANCE
                                    .SetBkColor(di.hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_HIGHLIGHT));
                        } else {
                            GDI32Ex.INSTANCE.SetTextColor(di.hDC,
                                    User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_MENUTEXT));
                            GDI32Ex.INSTANCE.SetBkColor(di.hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_MENU));
                        }
                        int x = di.rcItem.left;
                        int y = di.rcItem.top;

                        x += (getSystemMenuImageSize() + SPACE_ICONS) * 2;

                        GDI32.INSTANCE.SelectObject(di.hDC, getSystemMenuFont());
                        GDI32Ex.INSTANCE.ExtTextOut(di.hDC, x, y, GDI32Ex.ETO_OPAQUE, di.rcItem, mm.item.getText(),
                                mm.item.getText().length(), null);

                        x = di.rcItem.left;

                        if (mm.item instanceof JCheckBoxMenuItem) {
                            JCheckBoxMenuItem cc = (JCheckBoxMenuItem) mm.item;
                            if (cc.getState()) {
                                drawHBITMAP(di.hDC, x, y, checked.getWidth(), checked.getHeight(), hbitmapChecked);
                            } else {
                                drawHBITMAP(di.hDC, x, y, unchecked.getWidth(), unchecked.getHeight(), hbitmapUnchecked);
                            }
                        }

                        x += getSystemMenuImageSize() + SPACE_ICONS;

                        if (mm.hbm != null) {
                            drawHBITMAP(di.hDC, x, y, mm.icon.getIconWidth(), mm.icon.getIconHeight(), mm.hbm);
                        }

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
        public Icon icon;
        public HBITMAP hbm;
        public JMenuItem item;

        public MenuMap(JMenuItem item) {
            this.item = item;

            if (item.getIcon() != null) {
                icon = item.getIcon();
                hbm = getMenuImage(icon);
            }
        }

        protected void finalize() throws Throwable {
            close();

            super.finalize();
        }

        public void close() {
            if (hbm != null) {
                GDI32.INSTANCE.DeleteObject(hbm);
                hbm = null;
            }
        }

        public void fire() {
            item.doClick();
        }
    }

    static int count = 0;
    static MessagePump mp;

    boolean close = false;

    Icon iconTrayIcon;
    String title;
    JPopupMenu menu;

    BufferedImage checked;
    BufferedImage unchecked;
    HBITMAP hbitmapChecked;
    HBITMAP hbitmapUnchecked;
    HBITMAP hbitmapTrayIcon;
    HICON hicoTrayIcon;
    // free hmenus later, when close
    List<HMENU> hMenus = new ArrayList<HMENU>();
    // position in this list == id of HMENU item
    List<MenuMap> hMenusIDs = new ArrayList<MenuMap>();

    public WindowsSysTray() {
        create();

        try {
            checked = ImageIO.read(WindowsSysTray.class.getResourceAsStream("checked.png"));
            hbitmapChecked = createBitmap(checked);
            unchecked = ImageIO.read(WindowsSysTray.class.getResourceAsStream("unchecked.png"));
            hbitmapUnchecked = createBitmap(unchecked);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void create() {
        if (count == 0) {
            mp = new MessagePump();
            mp.start();
        }
        count++;
    }

    public void close() {
        if (hbitmapChecked != null) {
            GDI32.INSTANCE.DeleteObject(hbitmapChecked);
            hbitmapChecked = null;
        }
        if (hbitmapUnchecked != null) {
            GDI32.INSTANCE.DeleteObject(hbitmapUnchecked);
            hbitmapUnchecked = null;
        }

        if (hbitmapTrayIcon != null) {
            GDI32.INSTANCE.DeleteObject(hbitmapTrayIcon);
            hbitmapTrayIcon = null;
        }
        if (hicoTrayIcon != null) {
            GDI32.INSTANCE.DeleteObject(hicoTrayIcon);
            hicoTrayIcon = null;
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

    static void drawHBITMAP(HDC hDC, int x, int y, int cx, int cy, HBITMAP hbm) {
        HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(hDC);
        HANDLE h = GDI32.INSTANCE.SelectObject(hdc, hbm);
        if (!GDI32Ex.INSTANCE.BitBlt(hDC, x, y, cx, cy, hdc, 0, 0, GDI32Ex.SRCCOPY))
            throw new GetLastErrorException();
        GDI32.INSTANCE.SelectObject(hdc, h);
        if (!GDI32.INSTANCE.DeleteDC(hdc))
            throw new GetLastErrorException();
    }

    static LOGFONT getSystemMenuFont() {
        NONCLIENTMETRICS nm = new NONCLIENTMETRICS();

        User32Ex.INSTANCE.SystemParametersInfo(User32Ex.SPI_GETNONCLIENTMETRICS, 0, nm, 0);
        return nm.lfMenuFont;
    }

    static int getSystemMenuImageSize() {
        return User32.INSTANCE.GetSystemMetrics(SM_CYMENUCHECK);
    }

    static BufferedImage createBm(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    HBITMAP createBitmap(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return createBitmap(bi);
    }

    // https://github.com/twall/jna/blob/master/contrib/alphamaskdemo/com/sun/jna/contrib/demo/AlphaMaskDemo.java

    static HBITMAP createBitmap(BufferedImage image) {
        User32 user = User32.INSTANCE;
        GDI32 gdi = GDI32.INSTANCE;

        int w = image.getWidth(null);
        int h = image.getHeight(null);
        HDC screenDC = user.GetDC(null);
        HDC memDC = gdi.CreateCompatibleDC(screenDC);
        HBITMAP hBitmap = null;

        try {
            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g = (Graphics2D) buf.getGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
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

    static HBITMAP getMenuImage(Icon icon) {
        BufferedImage img = createBm(icon);

        int menubarHeigh = getSystemMenuImageSize();

        BufferedImage scaledImage = new BufferedImage(menubarHeigh, menubarHeigh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.drawImage(img, 0, 0, menubarHeigh, menubarHeigh, null);
        g.dispose();

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
        this.iconTrayIcon = icon;
    }

    public void setTitle(String t) {
        title = t;
    }

    void clearMenus() {
        for (HMENU hmenu : hMenus) {
            User32Ex.INSTANCE.DestroyMenu(hmenu);
        }
        hMenus.clear();
        for (MenuMap m : hMenusIDs) {
            m.close();
        }
        hMenusIDs.clear();
    }

    public void setMenu(JPopupMenu menu) {
        this.menu = menu;
    }

    void updateMenus() {
        clearMenus();

        HMENU hmenu = User32Ex.INSTANCE.CreatePopupMenu();
        hMenus.add(hmenu);

        for (int i = 0; i < menu.getComponentCount(); i++) {
            Component e = menu.getComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                HMENU hsub = createSubmenu(sub);
                hMenus.add(hsub);

                int nID = hMenusIDs.size();
                hMenusIDs.add(new MenuMap(sub));

                // you know, the usual windows tricks (transfer handle to
                // decimal)
                int handle = (int) Pointer.nativeValue(hsub.getPointer());

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MF_POPUP | MFT_OWNERDRAW, handle, null))
                    throw new GetLastErrorException();

                MENUITEMINFO mi = new MENUITEMINFO();
                if (!User32Ex.INSTANCE.GetMenuItemInfo(hmenu, handle, false, mi))
                    throw new GetLastErrorException();
                mi.dwItemData = new ULONG_PTR(nID);
                mi.fMask |= MENUITEMINFO.MIIM_DATA;
                if (!User32Ex.INSTANCE.SetMenuItemInfo(hmenu, handle, false, mi))
                    throw new GetLastErrorException();
            } else if (e instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                int nID = hMenusIDs.size();
                hMenusIDs.add(new MenuMap(ch));

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MFT_OWNERDRAW, nID, null))
                    throw new GetLastErrorException();

                MENUITEMINFO mmi = new MENUITEMINFO();
                if (!User32Ex.INSTANCE.GetMenuItemInfo(hmenu, nID, false, mmi))
                    throw new GetLastErrorException();
                mmi.dwItemData = new ULONG_PTR(nID);
                mmi.fMask |= MENUITEMINFO.MIIM_DATA;
                if (!User32Ex.INSTANCE.SetMenuItemInfo(hmenu, nID, false, mmi))
                    throw new GetLastErrorException();
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                int nID = hMenusIDs.size();
                hMenusIDs.add(new MenuMap(mi));

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MFT_OWNERDRAW, nID, null))
                    throw new GetLastErrorException();

                MENUITEMINFO mmi = new MENUITEMINFO();
                if (!User32Ex.INSTANCE.GetMenuItemInfo(hmenu, nID, false, mmi))
                    throw new GetLastErrorException();
                mmi.dwItemData = new ULONG_PTR(nID);
                mmi.fMask |= MENUITEMINFO.MIIM_DATA;
                if (!User32Ex.INSTANCE.SetMenuItemInfo(hmenu, nID, false, mmi))
                    throw new GetLastErrorException();
            }

            if (e instanceof JPopupMenu.Separator) {
                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MF_SEPARATOR, 0, null))
                    throw new GetLastErrorException();
            }
        }
    }

    HMENU createSubmenu(JMenu menu) {
        HMENU hmenu = User32Ex.INSTANCE.CreatePopupMenu();

        for (int i = 0; i < menu.getMenuComponentCount(); i++) {
            Component e = menu.getMenuComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                HMENU hsub = createSubmenu(sub);
                hMenus.add(hsub);

                // you know, the usual windows tricks (transfer handle to
                // decimal)
                int handle = (int) Pointer.nativeValue(hsub.getPointer());

                int nID = hMenusIDs.size();
                hMenusIDs.add(new MenuMap(sub));

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MF_POPUP | MFT_OWNERDRAW, handle, null))
                    throw new GetLastErrorException();

                MENUITEMINFO mi = new MENUITEMINFO();
                if (!User32Ex.INSTANCE.GetMenuItemInfo(hmenu, handle, false, mi))
                    throw new GetLastErrorException();
                mi.dwItemData = new ULONG_PTR(nID);
                mi.fMask |= MENUITEMINFO.MIIM_DATA;
                if (!User32Ex.INSTANCE.SetMenuItemInfo(hmenu, handle, false, mi))
                    throw new GetLastErrorException();
            } else if (e instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                int nID = hMenusIDs.size();
                hMenusIDs.add(new MenuMap(ch));

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MFT_OWNERDRAW, nID, null))
                    throw new GetLastErrorException();

                MENUITEMINFO mi = new MENUITEMINFO();
                if (!User32Ex.INSTANCE.GetMenuItemInfo(hmenu, nID, false, mi))
                    throw new GetLastErrorException();
                mi.dwItemData = new ULONG_PTR(nID);
                mi.fMask |= MENUITEMINFO.MIIM_DATA;
                if (!User32Ex.INSTANCE.SetMenuItemInfo(hmenu, nID, false, mi))
                    throw new GetLastErrorException();
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                int nID = hMenusIDs.size();
                hMenusIDs.add(new MenuMap(mi));

                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MFT_OWNERDRAW, nID, null))
                    throw new GetLastErrorException();

                MENUITEMINFO mmi = new MENUITEMINFO();
                if (!User32Ex.INSTANCE.GetMenuItemInfo(hmenu, nID, false, mmi))
                    throw new GetLastErrorException();
                mmi.dwItemData = new ULONG_PTR(nID);
                mmi.fMask |= MENUITEMINFO.MIIM_DATA;
                if (!User32Ex.INSTANCE.SetMenuItemInfo(hmenu, nID, false, mmi))
                    throw new GetLastErrorException();
            }

            if (e instanceof JPopupMenu.Separator) {
                if (!User32Ex.INSTANCE.AppendMenu(hmenu, MF_SEPARATOR, 0, null))
                    throw new GetLastErrorException();
            }
        }

        return hmenu;

    }

    public void show() {
        if (hbitmapTrayIcon != null) {
            GDI32.INSTANCE.DeleteObject(hbitmapTrayIcon);
            hbitmapTrayIcon = null;
        }
        if (hicoTrayIcon != null) {
            GDI32.INSTANCE.DeleteObject(hicoTrayIcon);
            hicoTrayIcon = null;
        }

        hbitmapTrayIcon = createBitmap(iconTrayIcon);
        hicoTrayIcon = createIconIndirect(hbitmapTrayIcon);

        NOTIFYICONDATA nid = new NOTIFYICONDATA();
        nid.setTooltip(title);
        nid.hWnd = mp.hWnd;
        nid.uCallbackMessage = WM_SHELLNOTIFY;
        nid.hIcon = hicoTrayIcon;

        if (!Shell32Ex.INSTANCE.Shell_NotifyIcon(Shell32Ex.NIM_ADD, nid))
            throw new GetLastErrorException();
    }

    public void update() {
        if (hbitmapTrayIcon != null) {
            GDI32.INSTANCE.DeleteObject(hbitmapTrayIcon);
            hbitmapTrayIcon = null;
        }
        if (hicoTrayIcon != null) {
            GDI32.INSTANCE.DeleteObject(hicoTrayIcon);
            hicoTrayIcon = null;
        }

        hbitmapTrayIcon = createBitmap(iconTrayIcon);
        hicoTrayIcon = createIconIndirect(hbitmapTrayIcon);

        NOTIFYICONDATA nid = new NOTIFYICONDATA();
        nid.setTooltip(title);
        nid.hWnd = mp.hWnd;
        nid.uCallbackMessage = WM_SHELLNOTIFY;
        nid.hIcon = hicoTrayIcon;

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
        if (!User32Ex.INSTANCE.TrackPopupMenu(hMenus.get(0), TPM_RIGHTBUTTON, p.x, p.y, 0, mp.hWnd, null))
            throw new GetLastErrorException();

        User32.INSTANCE.PostMessage(mp.hWnd, WM_NULL, null, null);
    }
}
