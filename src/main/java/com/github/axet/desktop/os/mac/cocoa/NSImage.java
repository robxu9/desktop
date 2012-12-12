package com.github.axet.desktop.os.mac.cocoa;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import com.github.axet.desktop.os.mac.foundation.Runtime;
import com.sun.jna.Pointer;

// https://developer.apple.com/library/mac/#documentation/Cocoa/Reference/ApplicationKit/Classes/NSImage_Class

public class NSImage extends NSObject {

    static Pointer klass = Runtime.INSTANCE.objc_lookUpClass("NSImage");

    static Pointer initWithData = Runtime.INSTANCE.sel_getUid("initWithData:");

    static BufferedImage createBitmap(Icon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    static NSData create(Icon icon) {
        BufferedImage bi = createBitmap(icon);
        return create(bi);
    }

    static NSData create(BufferedImage img) {
        try {
            ByteArrayOutputStream bufio = new ByteArrayOutputStream();
            ImageIO.write(img, "PNG", bufio);
            byte[] buf = bufio.toByteArray();
            NSData data = new NSData(buf);
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NSImage(Icon img) {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        NSData data = create(img);
        Runtime.INSTANCE.objc_msgSend(this, initWithData, data);

        retain();
    }

    public NSImage(BufferedImage img) {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        NSData data = create(img);
        Runtime.INSTANCE.objc_msgSend(this, initWithData, data);

        retain();
    }

    public NSImage(NSData data) {
        super(Runtime.INSTANCE.class_createInstance(klass, 0));

        Runtime.INSTANCE.objc_msgSend(this, initWithData, data);

        retain();
    }

    public NSImage(Pointer p) {
        super(Pointer.nativeValue(p));

        retain();
    }

    protected void finalize() throws Throwable {
        release();
    }

}