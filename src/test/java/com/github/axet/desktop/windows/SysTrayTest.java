package com.github.axet.desktop.windows;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import javax.swing.JFrame;

import net.sf.image4j.codec.ico.ICODecoder;

import com.github.axet.desktop.os.win.SysTrayIcon;

public class SysTrayTest {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Main App");

        try {
            InputStream is = SysTrayTest.class.getResourceAsStream("warn.ico");
            List<BufferedImage> bmp = ICODecoder.read(is);
            SysTrayIcon sys = new SysTrayIcon();
            sys.addIcon(bmp.get(0));
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame.setSize(new Dimension(100, 100));
        frame.setVisible(true);
    }
}
