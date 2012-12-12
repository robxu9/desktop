package com.github.axet.desktop.os.mac;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.axet.desktop.DesktopSysTray;
import com.github.axet.desktop.os.mac.cocoa.NSFont;
import com.github.axet.desktop.os.mac.cocoa.NSImage;
import com.github.axet.desktop.os.mac.cocoa.NSMenu;
import com.github.axet.desktop.os.mac.cocoa.NSMenuItem;
import com.github.axet.desktop.os.mac.cocoa.NSStatusBar;
import com.github.axet.desktop.os.mac.cocoa.NSStatusItem;
import com.github.axet.desktop.os.mac.cocoa.NSString;

public class OSXSysTray extends DesktopSysTray {

    BufferedImage icon;

    JPopupMenu menu;

    NSStatusItem statusItem;

    public final static int NSVariableStatusItemLength = -1;
    public final static int NSSquareStatusItemLength = -2;

    @Override
    public void showContextMenu() {
    }

    static BufferedImage createBitmap(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    @Override
    public void setIcon(Icon icon) {
        this.icon = createBitmap(icon);

        NSFont f = NSFont.menuBarFontOfSize(0);
        int menubarHeigh = (int) f.pointSize();

        BufferedImage scaledImage = new BufferedImage(menubarHeigh, menubarHeigh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(this.icon, 0, 0, menubarHeigh, menubarHeigh, null);
        graphics2D.dispose();

        this.icon = scaledImage;
    }

    NSImage getMenuImage(Icon icon) {
        BufferedImage img = createBitmap(icon);

        NSFont f = NSFont.menuFontOfSize(0);
        int menubarHeigh = (int) f.pointSize();

        BufferedImage scaledImage = new BufferedImage(menubarHeigh, menubarHeigh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(img, 0, 0, menubarHeigh, menubarHeigh, null);
        graphics2D.dispose();

        return new NSImage(scaledImage);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void show() {
        updateMenus();
    }

    void updateMenus() {
        if (statusItem == null) {
            NSStatusBar b = new NSStatusBar();
            statusItem = b.statusItemWithLength(NSVariableStatusItemLength);
        }

        NSMenu m = new NSMenu();
        m.setAutoenablesItems(false);
        NSImage n = new NSImage(icon);

        statusItem.setImage(n);
        statusItem.setHighlightMode(true);
        statusItem.setMenu(m);

        for (int i = 0; i < menu.getComponentCount(); i++) {
            Component e = menu.getComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                NSMenu hsub = createSubmenu(sub);

                NSImage bm = null;
                if (sub.getIcon() != null)
                    bm = getMenuImage(sub.getIcon());

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(sub.getText()));
                item.setImage(bm);
                item.setSubmenu(hsub);
                m.addItem(item);
            } else if (e instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                NSImage bm = null;
                if (ch.getIcon() != null)
                    bm = getMenuImage(ch.getIcon());

                OSXSysTrayAction action = new OSXSysTrayAction(ch);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(ch.getText()));
                item.setImage(bm);
                item.setEnabled(ch.isEnabled());
                item.setState(ch.getState() ? 1 : 0);
                item.setAction(OSXSysTrayAction.action);
                item.setTarget(action);
                m.addItem(item);
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                NSImage bm = null;
                if (mi.getIcon() != null)
                    bm = getMenuImage(mi.getIcon());

                OSXSysTrayAction action = new OSXSysTrayAction(mi);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(mi.getText()));
                item.setImage(bm);
                item.setEnabled(mi.isEnabled());
                item.setAction(OSXSysTrayAction.action);
                item.setTarget(action);
                m.addItem(item);
            }

            if (e instanceof JPopupMenu.Separator) {
                m.addItem(NSMenuItem.separatorItem());
            }
        }
    }

    NSMenu createSubmenu(JMenu menu) {
        NSMenu m = new NSMenu();

        for (int i = 0; i < menu.getMenuComponentCount(); i++) {
            Component e = menu.getMenuComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                NSMenu hsub2 = createSubmenu(sub);

                NSImage bm = null;
                if (sub.getIcon() != null)
                    bm = getMenuImage(sub.getIcon());

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(sub.getText()));
                item.setImage(bm);
                item.setSubmenu(hsub2);
                m.addItem(item);
            } else if (e instanceof JCheckBoxMenuItem) {
                JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                NSImage bm = null;
                if (ch.getIcon() != null)
                    bm = getMenuImage(ch.getIcon());

                OSXSysTrayAction action = new OSXSysTrayAction(ch);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(ch.getText()));
                item.setImage(bm);
                item.setEnabled(ch.isEnabled());
                item.setState(ch.getState() ? 1 : 0);
                item.setAction(OSXSysTrayAction.action);
                item.setTarget(action);
                m.addItem(item);
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                NSImage bm = null;
                if (mi.getIcon() != null)
                    bm = getMenuImage(mi.getIcon());

                OSXSysTrayAction action = new OSXSysTrayAction(mi);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(mi.getText()));
                item.setImage(bm);
                item.setEnabled(mi.isEnabled());
                item.setAction(OSXSysTrayAction.action);
                item.setTarget(action);
                m.addItem(item);

            }

            if (e instanceof JPopupMenu.Separator) {
                m.addItem(NSMenuItem.separatorItem());
            }
        }

        return m;
    }

    @Override
    public void update() {
        updateMenus();
    }

    @Override
    public void hide() {
        if (statusItem != null) {
            NSStatusBar b = new NSStatusBar();
            b.removeStatusItem(statusItem);
            statusItem = null;
        }
    }

    @Override
    public void setMenu(JPopupMenu menu) {
        this.menu = menu;
    }

    @Override
    public void close() {
        hide();
    }

}
