package com.github.axet.desktop.os.mac;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.github.axet.desktop.DesktopSysTray;
import com.github.axet.desktop.os.mac.cocoa.NSData;
import com.github.axet.desktop.os.mac.cocoa.NSImage;
import com.github.axet.desktop.os.mac.cocoa.NSMenu;
import com.github.axet.desktop.os.mac.cocoa.NSMenuItem;
import com.github.axet.desktop.os.mac.cocoa.NSStatusBar;
import com.github.axet.desktop.os.mac.cocoa.NSStatusItem;
import com.github.axet.desktop.os.mac.cocoa.NSString;

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

    BufferedImage createBitmap(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    void assertEquals(Object o, Object b) {

    }

    @Override
    public void show() {
//        try {
//            BufferedImage bi = createBitmap(icon);
//            ByteArrayOutputStream bufio = new ByteArrayOutputStream();
//            ImageIO.write(bi, "JPG", bufio);
//            byte[] buf = bufio.toByteArray();
//            NSData data = new NSData(buf);
//            NSImage n = new NSImage(data);

            NSMenu m = new NSMenu();

            NSMenuItem item = new NSMenuItem();
            item.setTitle(new NSString("Test"));
            m.addItem(item);

            NSStatusBar b = new NSStatusBar();
            NSStatusItem i = b.statusItemWithLength(NSVariableStatusItemLength);
            i.setTitle(new NSString("absdgasfds afdsaf ad"));
//            i.setImage(n);
            i.setHighlightMode(true);
            i.setMenu(m);
            
            JOptionPane.showMessageDialog(null, "done");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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
