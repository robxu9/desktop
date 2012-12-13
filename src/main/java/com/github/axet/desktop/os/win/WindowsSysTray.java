package com.github.axet.desktop.os.win;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
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
import com.github.axet.desktop.Utils;
import com.github.axet.desktop.os.win.handle.BLENDFUNCTION;
import com.github.axet.desktop.os.win.handle.DRAWITEMSTRUCT;
import com.github.axet.desktop.os.win.handle.LOGFONT;
import com.github.axet.desktop.os.win.handle.MEASUREITEMSTRUCT;
import com.github.axet.desktop.os.win.handle.MENUITEMINFO;
import com.github.axet.desktop.os.win.handle.NONCLIENTMETRICS;
import com.github.axet.desktop.os.win.handle.NOTIFYICONDATA;
import com.github.axet.desktop.os.win.handle.WNDPROC;
import com.github.axet.desktop.os.win.libs.GDI32Ex;
import com.github.axet.desktop.os.win.libs.Msimg32;
import com.github.axet.desktop.os.win.libs.Shell32Ex;
import com.github.axet.desktop.os.win.libs.User32Ex;
import com.github.axet.desktop.os.win.wrap.HBitmapWrap;
import com.github.axet.desktop.os.win.wrap.HIconWrap;
import com.github.axet.desktop.os.win.wrap.WndClassExWrap;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HFONT;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.HMENU;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.SIZE;

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

        WndClassExWrap wc;
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
                        SIZE size = measureItem(hWnd, mm);
                        ms.itemWidth = size.cx;
                        ms.itemHeight = size.cy;
                        ms.write();
                        break;
                    }
                    case WM_DRAWITEM: {
                        DRAWITEMSTRUCT di = new DRAWITEMSTRUCT(new Pointer(lParam.longValue()));
                        MenuMap mm = hMenusIDs.get(di.itemData.intValue());

                        drawItem(mm, di.hDC, di.rcItem, di.itemState);
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

            wc = new WndClassExWrap(hInstance, WndProc, "SystemTrayIcon");

            HWND hwnd = User32Ex.INSTANCE.CreateWindowEx(0, wc.getName(), wc.getName(), User32Ex.WS_OVERLAPPEDWINDOW,
                    0, 0, 0, 0, null, null, hInstance, null);

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

        SIZE measureItem(HWND hWnd, MenuMap mm) {
            HDC hdc = User32.INSTANCE.GetDC(hWnd);
            HFONT hfntOld = (HFONT) GDI32.INSTANCE.SelectObject(hdc, getSystemMenuFont());
            SIZE size = new SIZE();
            if (!GDI32Ex.INSTANCE.GetTextExtentPoint32(hdc, mm.item.getText(), mm.item.getText().length(), size))
                throw new GetLastErrorException();
            GDI32.INSTANCE.SelectObject(hdc, hfntOld);
            User32.INSTANCE.ReleaseDC(hWnd, hdc);

            size.cx += (getSystemMenuImageSize() + SPACE_ICONS) * 2;

            return size;
        }

        void destory() {
            if (hWnd != null) {
                if (!User32Ex.INSTANCE.DestroyWindow(hWnd))
                    throw new GetLastErrorException();
                hWnd = null;
            }

            if (wc != null) {
                wc.close();
                wc = null;
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
        public HBitmapWrap hbm;
        public JMenuItem item;

        public MenuMap(JMenuItem item) {
            this.item = item;

            if (item.getIcon() != null) {
                Icon icon = item.getIcon();
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

    String title;
    JPopupMenu menu;

    HBitmapWrap hbitmapChecked;
    HBitmapWrap hbitmapUnchecked;
    HBitmapWrap hbitmapTrayIcon;
    HIconWrap hicoTrayIcon;
    // free HMENU later, when close
    List<HMENU> hMenus = new ArrayList<HMENU>();
    // position in this list == id of HMENU item
    List<MenuMap> hMenusIDs = new ArrayList<MenuMap>();

    public WindowsSysTray() {
        create();

        try {
            BufferedImage checked = ImageIO.read(WindowsSysTray.class.getResourceAsStream("checked.png"));
            hbitmapChecked = new HBitmapWrap(checked);
            BufferedImage unchecked = ImageIO.read(WindowsSysTray.class.getResourceAsStream("unchecked.png"));
            hbitmapUnchecked = new HBitmapWrap(unchecked);
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
            hbitmapChecked.close();
            hbitmapChecked = null;
        }
        if (hbitmapUnchecked != null) {
            hbitmapUnchecked.close();
            hbitmapUnchecked = null;
        }

        if (hbitmapTrayIcon != null) {
            hbitmapTrayIcon.close();
            hbitmapTrayIcon = null;
        }
        if (hicoTrayIcon != null) {
            hicoTrayIcon.close();
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

    static void drawHBITMAP(HBITMAP hbm, int x, int y, int cx, int cy, HDC hdcDst) {
        HDC hdcSrc = GDI32.INSTANCE.CreateCompatibleDC(hdcDst);
        HANDLE h = GDI32.INSTANCE.SelectObject(hdcSrc, hbm);

        BLENDFUNCTION.ByValue bld = new BLENDFUNCTION.ByValue();
        bld.BlendOp = WinUser.AC_SRC_OVER;
        bld.BlendFlags = 0;
        bld.SourceConstantAlpha = (byte) 255;
        bld.AlphaFormat = WinUser.AC_SRC_ALPHA;

        if (!Msimg32.INSTANCE.AlphaBlend(hdcDst, x, y, cx, cy, hdcSrc, 0, 0, cx, cy, bld))
            throw new GetLastErrorException();
        GDI32.INSTANCE.SelectObject(hdcSrc, h);
        if (!GDI32.INSTANCE.DeleteDC(hdcSrc))
            throw new GetLastErrorException();
    }

    void drawItem(MenuMap mm, HDC hDC, RECT rcItem, int itemState) {
        if (!mm.item.isEnabled()) {
            GDI32Ex.INSTANCE.SetTextColor(hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_GRAYTEXT));
            GDI32Ex.INSTANCE.SetBkColor(hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_MENU));
        } else if ((itemState & DRAWITEMSTRUCT.ODS_SELECTED) == DRAWITEMSTRUCT.ODS_SELECTED) {
            GDI32Ex.INSTANCE.SetTextColor(hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_HIGHLIGHTTEXT));
            GDI32Ex.INSTANCE.SetBkColor(hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_HIGHLIGHT));
        } else {
            GDI32Ex.INSTANCE.SetTextColor(hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_MENUTEXT));
            GDI32Ex.INSTANCE.SetBkColor(hDC, User32Ex.INSTANCE.GetSysColor(User32Ex.COLOR_MENU));
        }
        int x = rcItem.left;
        int y = rcItem.top;

        x += (getSystemMenuImageSize() + SPACE_ICONS) * 2;

        GDI32.INSTANCE.SelectObject(hDC, getSystemMenuFont());
        GDI32Ex.INSTANCE.ExtTextOut(hDC, x, y, GDI32Ex.ETO_OPAQUE, rcItem, mm.item.getText(), mm.item.getText()
                .length(), null);

        x = rcItem.left;

        if (mm.item instanceof JCheckBoxMenuItem) {
            JCheckBoxMenuItem cc = (JCheckBoxMenuItem) mm.item;
            if (cc.getState()) {
                drawHBITMAP(hbitmapChecked, x, y, hbitmapChecked.getImage().getWidth(), hbitmapChecked.getImage()
                        .getHeight(), hDC);
            } else {
                drawHBITMAP(hbitmapUnchecked, x, y, hbitmapUnchecked.getImage().getWidth(), hbitmapUnchecked.getImage()
                        .getHeight(), hDC);
            }
        }

        x += getSystemMenuImageSize() + SPACE_ICONS;

        if (mm.hbm != null) {
            drawHBITMAP(mm.hbm, x, y, mm.hbm.getImage().getWidth(), mm.hbm.getImage().getHeight(), hDC);
        }
    }

    static LOGFONT getSystemMenuFont() {
        NONCLIENTMETRICS nm = new NONCLIENTMETRICS();

        User32Ex.INSTANCE.SystemParametersInfo(User32Ex.SPI_GETNONCLIENTMETRICS, 0, nm, 0);
        return nm.lfMenuFont;
    }

    static int getSystemMenuImageSize() {
        return User32.INSTANCE.GetSystemMetrics(SM_CYMENUCHECK);
    }

    static HBitmapWrap getMenuImage(Icon icon) {
        BufferedImage img = Utils.createBitmap(icon);

        int menubarHeigh = getSystemMenuImageSize();

        BufferedImage scaledImage = new BufferedImage(menubarHeigh, menubarHeigh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.drawImage(img, 0, 0, menubarHeigh, menubarHeigh, null);
        g.dispose();

        return new HBitmapWrap(scaledImage);
    }

    public void setIcon(Icon icon) {
        this.hbitmapTrayIcon = new HBitmapWrap(Utils.createBitmap(icon));
        this.hicoTrayIcon = new HIconWrap(hbitmapTrayIcon);

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
        NOTIFYICONDATA nid = new NOTIFYICONDATA();
        nid.setTooltip(title);
        nid.hWnd = mp.hWnd;
        nid.uCallbackMessage = WM_SHELLNOTIFY;
        nid.hIcon = hicoTrayIcon;

        if (!Shell32Ex.INSTANCE.Shell_NotifyIcon(Shell32Ex.NIM_ADD, nid))
            throw new GetLastErrorException();
    }

    public void update() {

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
