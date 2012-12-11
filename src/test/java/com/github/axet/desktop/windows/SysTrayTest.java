    package com.github.axet.desktop.windows;
    
    import java.awt.Dimension;
    import java.awt.GridBagConstraints;
    import java.awt.GridBagLayout;
    import java.awt.Insets;
    import java.awt.MouseInfo;
    import java.awt.Point;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.image.BufferedImage;
    import java.io.InputStream;
    import java.util.List;
    
    import javax.swing.ImageIcon;
    import javax.swing.JButton;
    import javax.swing.JCheckBoxMenuItem;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JMenu;
    import javax.swing.JMenuItem;
    import javax.swing.JPopupMenu;
    
    import net.sf.image4j.codec.ico.ICODecoder;
    
    import com.github.axet.desktop.Desktop;
    import com.github.axet.desktop.DesktopSysTray;
    import com.github.axet.desktop.os.win.WindowsSysTray;
    
    public class SysTrayTest extends JFrame {
    
        DesktopSysTray sys = Desktop.getDesktopSysTray();
        ImageIcon warn;
        ImageIcon stop;
        JPopupMenu menu;
    
        DesktopSysTray.Listener ml = new DesktopSysTray.Listener() {
            @Override
            public void mouseLeftClick() {
                System.out.println("left click");
            }
    
            @Override
            public void mouseLeftDoubleClick() {
                System.out.println("double click");
            }
    
            @Override
            public void mouseRightClick() {
                sys.showContextMenu();
            }
    
        };
    
        public SysTrayTest() {
            super("MainFrame");
    
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
            try {
                InputStream is = SysTrayTest.class.getResourceAsStream("bug.ico");
                List<BufferedImage> bmp = ICODecoder.read(is);
                warn = new ImageIcon(bmp.get(0));
    
                is = SysTrayTest.class.getResourceAsStream("dov.ico");
                bmp = ICODecoder.read(is);
                stop = new ImageIcon(bmp.get(0));
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            menu = new JPopupMenu();
            JMenuItem menuItem1 = new JMenuItem("test1", warn);
            menuItem1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test1");
                }
            });
            menu.add(menuItem1);
            menu.addSeparator();
    
            JMenu menu2 = new JMenu("submenu");
            menu2.setIcon(stop);
            JMenuItem menuItem21 = new JMenuItem("test21", warn);
            menuItem21.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test21");
                }
            });
            menu2.add(menuItem21);
            JMenuItem menuItem22 = new JMenuItem("test22", warn);
            menuItem22.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test22");
                }
            });
            menu2.add(menuItem22);
    
            JMenu menu3 = new JMenu("submenu");
            menu3.setIcon(stop);
            JMenuItem menuItem31 = new JMenuItem("test31", warn);
            menuItem31.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test31");
                }
            });
            menu3.add(menuItem31);
            JMenuItem menuItem32 = new JMenuItem("test32", warn);
            menuItem32.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test32");
                }
            });
            menu3.add(menuItem32);
            menu2.add(menu3);
    
            menu2.addSeparator();
            JMenuItem menuItem23 = new JMenuItem("test23", warn);
            menuItem23.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test23");
                }
            });
            menu2.add(menuItem23);
    
            menu.add(menu2);
            menu.addSeparator();
            JMenuItem menuItem2 = new JMenuItem("test2", stop);
            menuItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test2");
                }
            });
            menu.add(menuItem2);
            JMenuItem menuItem3 = new JMenuItem("test3", warn);
            menuItem3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test3");
                }
            });
            menu.add(menuItem3);
    
            JMenuItem menuItem4 = new JMenuItem("test4", warn);
            menuItem4.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test4");
                }
            });
            menuItem4.setEnabled(false);
            menu.add(menuItem4);
    
            JCheckBoxMenuItem menuItem5 = new JCheckBoxMenuItem("test5", warn);
            menuItem5.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test5");
                }
            });
            menu.add(menuItem5);
    
            sys.addListener(ml);
            sys.setIcon(warn);
            sys.setTitle("Java tool2");
            sys.setMenu(menu);
            sys.show();
    
            this.setSize(new Dimension(400, 400));
            GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
            gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
            gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
            gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
            getContentPane().setLayout(gridBagLayout);
    
            JLabel lblNewLabel = new JLabel("Warn Label");
            lblNewLabel.setIcon(warn);
            GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
            gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel.gridx = 0;
            gbc_lblNewLabel.gridy = 0;
            getContentPane().add(lblNewLabel, gbc_lblNewLabel);
    
            JLabel lblNewLabel_1 = new JLabel("Stop Label");
            lblNewLabel_1.setIcon(stop);
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
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
            gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
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
                    sys = new WindowsSysTray();
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
            gbc_btnClose.insets = new Insets(0, 0, 0, 5);
            gbc_btnClose.gridx = 1;
            gbc_btnClose.gridy = 3;
            getContentPane().add(btnClose, gbc_btnClose);
    
            final JFrame that = this;
    
            JButton btnShowJmenu = new JButton("Show jmenu");
            btnShowJmenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    menu.show(that, 100, 100);
                }
            });
            GridBagConstraints gbc_btnShowJmenu = new GridBagConstraints();
            gbc_btnShowJmenu.gridx = 3;
            gbc_btnShowJmenu.gridy = 3;
            getContentPane().add(btnShowJmenu, gbc_btnShowJmenu);
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
            new SysTrayTest();
        }
    }
