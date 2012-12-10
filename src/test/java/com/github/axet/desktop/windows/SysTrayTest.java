package com.github.axet.desktop.windows;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sf.image4j.codec.ico.ICODecoder;

import com.github.axet.desktop.os.win.SysTrayIcon;

public class SysTrayTest extends JFrame {

    SysTrayIcon sys = new SysTrayIcon();
    BufferedImage warn;
    BufferedImage stop;

    SysTrayIcon.Listener ml = new SysTrayIcon.Listener() {
        @Override
        public void mouseLeftClick() {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseLeftDoubleClick() {
            System.out.println("double click");
        }

        @Override
        public void mouseRightClick() {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseRightDoubleClick() {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseRightUp() {
            System.out.println("right up");
            
        }
    };

    public SysTrayTest() {
        super("MainFrame");

        try {
            InputStream is = SysTrayTest.class.getResourceAsStream("bug.ico");
            List<BufferedImage> bmp = ICODecoder.read(is);
            warn = bmp.get(0);

            is = SysTrayTest.class.getResourceAsStream("dov.ico");
            bmp = ICODecoder.read(is);
            stop = bmp.get(0);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sys.addListener(ml);
        sys.setIcon(warn);
        sys.setTitle("Java tool2");
        sys.show();

        this.setSize(new Dimension(400, 400));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        getContentPane().setLayout(gridBagLayout);

        JLabel lblNewLabel = new JLabel("Warn Label");
        lblNewLabel.setIcon(new ImageIcon(warn));
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        getContentPane().add(lblNewLabel, gbc_lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Stop Label");
        lblNewLabel_1.setIcon(new ImageIcon(stop));
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_1.gridx = 1;
        gbc_lblNewLabel_1.gridy = 0;
        getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);

        JButton btnNewButton = new JButton("Warn");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                sys.setIcon(warn);
                sys.update();
            }
        });
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton.gridx = 0;
        gbc_btnNewButton.gridy = 1;
        getContentPane().add(btnNewButton, gbc_btnNewButton);

        JButton btnNewButton_1 = new JButton("Stop");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                sys.setIcon(stop);
                sys.update();
            }
        });
        GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
        gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
        gbc_btnNewButton_1.gridx = 1;
        gbc_btnNewButton_1.gridy = 1;
        getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sys.close();
            }
        });

        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sys = new SysTrayIcon();
                sys.addListener(ml);
                sys.setIcon(warn);
                sys.setTitle("Java tool2");
                sys.show();
            }
        });
        GridBagConstraints gbc_btnCreate = new GridBagConstraints();
        gbc_btnCreate.insets = new Insets(0, 0, 0, 5);
        gbc_btnCreate.gridx = 0;
        gbc_btnCreate.gridy = 3;
        getContentPane().add(btnCreate, gbc_btnCreate);
        GridBagConstraints gbc_btnClose = new GridBagConstraints();
        gbc_btnClose.gridx = 1;
        gbc_btnClose.gridy = 3;
        getContentPane().add(btnClose, gbc_btnClose);
        this.setVisible(true);

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
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SysTrayTest();
            }
        });
    }
}
