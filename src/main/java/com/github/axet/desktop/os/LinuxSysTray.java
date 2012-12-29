package com.github.axet.desktop.os;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.github.axet.desktop.DesktopSysTray;
import com.github.axet.desktop.Utils;

/**
 * System Tray Protocol Specification
 * 
 * http://standards.freedesktop.org/systemtray-spec/systemtray-spec-latest.html
 * 
 */

public class LinuxSysTray extends DesktopSysTray {

    SystemTray tray = SystemTray.getSystemTray();
    PopupMenu popup;
    BufferedImage image;

    TrayIcon trayIcon;
    JPopupMenu menu;
    String title;
    Icon icon;

    @Override
    public void setIcon(Icon icon) {
        this.icon = icon;

        image = Utils.createBitmap(icon);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void show() {
        if (trayIcon == null) {
            trayIcon = new TrayIcon(image, title, null);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }

        update();
    }

    @Override
    public void update() {
        updateMenus();

        trayIcon.setImage(image);
        trayIcon.setPopupMenu(popup);
    }

    void updateMenus() {
        popup = new PopupMenu();

        for (int i = 0; i < menu.getComponentCount(); i++) {
            Component e = menu.getComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                Menu ss = createSubmenu(sub);
                popup.add(ss);
            } else if (e instanceof JCheckBoxMenuItem) {
                final JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                final CheckboxMenuItem mm = new CheckboxMenuItem(ch.getText(), ch.getState());
                mm.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        ch.doClick();
                        updateMenus();
                    }
                });
                popup.add(mm);
            } else if (e instanceof JMenuItem) {
                final JMenuItem mi = (JMenuItem) e;

                final MenuItem mm = new MenuItem(mi.getText());
                mm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mi.doClick();
                    }
                });
                popup.add(mm);
            }

            if (e instanceof JPopupMenu.Separator) {
                popup.insertSeparator(popup.getItemCount());
            }
        }
    }

    Menu createSubmenu(JMenu menu) {
        Menu popup = new Menu(menu.getText());

        for (int i = 0; i < menu.getMenuComponentCount(); i++) {
            Component e = menu.getMenuComponent(i);

            if (e instanceof JMenu) {
                JMenu sub = (JMenu) e;
                Menu ss = createSubmenu(sub);
                popup.add(ss);
            } else if (e instanceof JCheckBoxMenuItem) {
                final JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e;

                final CheckboxMenuItem mm = new CheckboxMenuItem(ch.getText());
                mm.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        ch.doClick();
                        updateMenus();
                    }
                });
                popup.add(mm);
            } else if (e instanceof JMenuItem) {
                final JMenuItem mi = (JMenuItem) e;

                final MenuItem mm = new MenuItem(mi.getText());
                mm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mi.doClick();
                    }
                });
                popup.add(mm);
            }

            if (e instanceof JPopupMenu.Separator) {
                popup.insertSeparator(popup.getItemCount());
            }
        }

        return popup;

    }

    @Override
    public void hide() {
        if (trayIcon != null) {
            tray.remove(trayIcon);
            trayIcon = null;
        }
    }

    @Override
    public void setMenu(JPopupMenu menu) {
        this.menu = menu;
    }

    @Override
    public void close() {
        hide();
        tray = null;
    }

}
