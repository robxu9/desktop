    package com.github.axet.desktop;
    
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    
    import javax.swing.JFrame;
    import javax.swing.JMenuItem;
    import javax.swing.JPopupMenu;
    
    public class SimpleTrayTest extends JFrame {
    
        DesktopSysTray sys = Desktop.getDesktopSysTray();
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
                System.out.println("right click");
                sys.showContextMenu();
            }
    
        };
    
        public SimpleTrayTest() {
            super("MainFrame");
    
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
            menu = new JPopupMenu();
            JMenuItem menuItem1 = new JMenuItem("test1");
            menuItem1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("test1");
                }
            });
            menu.addSeparator();
    
            sys.addListener(ml);
            sys.setTitle("Java tool2");
            sys.setMenu(menu);
            sys.show();
        }
    
        public static void main(String[] args) {
            new SimpleTrayTest();
        }
    }
