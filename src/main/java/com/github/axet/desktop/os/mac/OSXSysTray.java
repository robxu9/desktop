package com.github.axet.desktop.os.mac;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.axet.desktop.DesktopSysTray;
import com.github.axet.desktop.Utils;
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
    ArrayList<OSXSysTrayAction> menuActions = new ArrayList<OSXSysTrayAction>();

    NSStatusItem statusItem;

    @Override
    public void setIcon(Icon icon) {
        this.icon = Utils.createBitmap(icon);

        NSFont f = NSFont.menuBarFontOfSize(0);
        int menubarHeigh = (int) f.pointSize();

        BufferedImage scaledImage = new BufferedImage(menubarHeigh, menubarHeigh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.drawImage(this.icon, 0, 0, menubarHeigh, menubarHeigh, null);
        g.dispose();

        this.icon = scaledImage;
    }

    NSImage getMenuImage(Icon icon) {
        BufferedImage img = Utils.createBitmap(icon);

        NSFont f = NSFont.menuFontOfSize(0);
        int menubarHeigh = (int) f.pointSize();

        BufferedImage scaledImage = new BufferedImage(menubarHeigh, menubarHeigh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.drawImage(img, 0, 0, menubarHeigh, menubarHeigh, null);
        g.dispose();

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
            NSStatusBar b = NSStatusBar.systemStatusBar();
            statusItem = b.statusItemWithLength(NSStatusBar.NSVariableStatusItemLength);
        }

        menuActions.clear();

        NSMenu m = new NSMenu();

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
                menuActions.add(action);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(ch.getText()));
                item.setImage(bm);
                item.setEnabled(ch.isEnabled());
                item.setState(ch.getState() ? 1 : 0);
                item.setTarget(action);
                item.setAction(OSXSysTrayAction.action);
                m.addItem(item);
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                NSImage bm = null;
                if (mi.getIcon() != null)
                    bm = getMenuImage(mi.getIcon());

                OSXSysTrayAction action = new OSXSysTrayAction(mi);
                menuActions.add(action);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(mi.getText()));
                item.setImage(bm);
                item.setEnabled(mi.isEnabled());
                item.setTarget(action);
                item.setAction(OSXSysTrayAction.action);
                m.addItem(item);
            }

            if (e instanceof JPopupMenu.Separator) {
                m.addItem(NSMenuItem.separatorItem());
            }
        }

        m.setAutoenablesItems(false);
        NSImage n = new NSImage(icon);
        statusItem.setImage(n);
        statusItem.setHighlightMode(true);
        statusItem.setMenu(m);
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
                menuActions.add(action);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(ch.getText()));
                item.setImage(bm);
                item.setEnabled(ch.isEnabled());
                item.setState(ch.getState() ? 1 : 0);
                item.setTarget(action);
                item.setAction(OSXSysTrayAction.action);
                m.addItem(item);
            } else if (e instanceof JMenuItem) {
                JMenuItem mi = (JMenuItem) e;

                NSImage bm = null;
                if (mi.getIcon() != null)
                    bm = getMenuImage(mi.getIcon());

                OSXSysTrayAction action = new OSXSysTrayAction(mi);
                menuActions.add(action);

                NSMenuItem item = new NSMenuItem();
                item.setTitle(new NSString(mi.getText()));
                item.setImage(bm);
                item.setEnabled(mi.isEnabled());
                item.setTarget(action);
                item.setAction(OSXSysTrayAction.action);
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
            NSStatusBar b = NSStatusBar.systemStatusBar();
            b.removeStatusItem(statusItem);
            statusItem = null;
            menuActions.clear();
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
