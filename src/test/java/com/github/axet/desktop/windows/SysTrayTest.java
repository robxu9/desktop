package com.github.axet.desktop.windows;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import javax.swing.JFrame;

import net.sf.image4j.codec.ico.ICODecoder;

import com.github.axet.desktop.os.win.SysTrayIcon;

public class SysTrayTest extends JFrame {

    final SysTrayIcon sys = new SysTrayIcon();
    BufferedImage warn;
    BufferedImage stop;

    public SysTrayTest() {
        super("MainFrame");

        this.setSize(new Dimension(400, 400));
        this.setVisible(true);

        try {
            InputStream is = SysTrayTest.class.getResourceAsStream("warn.ico");
            List<BufferedImage> bmp = ICODecoder.read(is);
            warn = bmp.get(0);

            is = SysTrayTest.class.getResourceAsStream("stop.ico");
            bmp = ICODecoder.read(is);
            stop = bmp.get(0);

            sys.setIcon(warn);
            sys.setTitle("Java tool2");

            sys.show();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void swIcons() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    sys.setIcon(warn);
                    sys.update();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    sys.setIcon(stop);
                    sys.update();

                }
            }
        });
        t.start();
    }

    public void blkIcons() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    sys.show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    sys.hide();

                }
            }
        });
        t.start();
    }

    public static void main(String[] args) {
        new SysTrayTest();
    }
}
