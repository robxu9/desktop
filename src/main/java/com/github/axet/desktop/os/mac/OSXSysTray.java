package com.github.axet.desktop.os.mac;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.github.axet.desktop.DesktopSysTray;
import com.github.axet.desktop.os.mac.cocoa.NSApplication;
import com.github.axet.desktop.os.mac.cocoa.NSDocTile;
import com.github.axet.desktop.os.mac.cocoa.NSImage;
import com.github.axet.desktop.os.mac.cocoa.NSMenu;
import com.github.axet.desktop.os.mac.cocoa.NSMenuItem;
import com.github.axet.desktop.os.mac.cocoa.NSRect;
import com.github.axet.desktop.os.mac.cocoa.NSStatusBar;
import com.github.axet.desktop.os.mac.cocoa.NSStatusItem;
import com.github.axet.desktop.os.mac.cocoa.NSString;
import com.github.axet.desktop.os.mac.cocoa.NSWindow;

public class OSXSysTray extends DesktopSysTray {

    Icon icon;

    public final static int NSVariableStatusItemLength = -1;
    public final static int NSSquareStatusItemLength = -2;

    @Override
    public void showContextMenu() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public void setTitle(String title) {
        // TODO Auto-generated method stub

    }

    void assertEquals(Object o, Object b) {

    }

    @Override
    public void show() {
        NSMenu m = new NSMenu();

        NSImage n = new NSImage(icon);

        NSApplication a = new NSApplication();

        NSDocTile nd = a.dockTile();
        nd.setBadgeLabel(new NSString("test"));

        boolean bb = a.isRunning();
        bb = a.isActive();
        long aa = a.requestUserAttention(NSApplication.NSCriticalRequest);
        a.setApplicationIconImage(n);
        a.cancelUserAttentionRequest(aa);

        NSRect rect = new NSRect(0, 0, 200, 200);
        NSWindow w = NSWindow.initWithContentRectStyleMaskBackingDefer(new NSRect.ByValue(rect),
                NSWindow.NSBorderlessWindowMask, NSWindow.NSBackingStoreBuffered, false);
        w.makeKeyAndOrderFront(a);

        NSMenuItem item = new NSMenuItem();
        item.setTitle(new NSString("Test"));
        m.addItem(item);

        NSStatusBar b = new NSStatusBar();
        NSStatusItem i = b.statusItemWithLength(NSVariableStatusItemLength);
        i.setTitle(new NSString("test"));
        // i.setImage(n);
        i.setHighlightMode(true);
        i.setMenu(m);

        JOptionPane.showMessageDialog(null, "asdfsdf");

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMenu(JPopupMenu menu) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
