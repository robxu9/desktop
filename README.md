# Desktop

Java desktop functions. Have you tried to find user default Download folder using java? If so, you would find this
library very helpful.

## Example Desktop Folders
    
    public class DesktopTest {
        public static void main(String[] args) {
            DesktopFolders d = Desktop.getDesktopFolders();
    
            System.out.println("Home: " + d.getHome());
            System.out.println("Documents: " + d.getDocuments());
            System.out.println("AppFolder: " + d.getAppData());
            System.out.println("Desktop: " + d.getDesktop());
            System.out.println("Downloads: " + d.getDownloads());
        }
    }

## Example Sys Tray Icon
(aka Notification Area Icons or Status Bar icons)

    package com.github.axet.desktop;
    
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    
    import javax.swing.JFrame;
    import javax.swing.JMenuItem;
    import javax.swing.JPopupMenu;
    
    public class SysTrayTest extends JFrame {
    
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
    
        public SysTrayTest() {
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
            new SysTrayTest();
        }
    }

## Example Power Events
    
    import com.github.axet.desktop.Desktop;
    import com.github.axet.desktop.DesktopPower;
    
    public class DesktopPowerTest {
        public static void main(String[] args) {
            DesktopPower d = Desktop.getDesktopPower();
            d.addListener(new DesktopPower.Listener() {
                @Override
                public void quit() {
                    // logout / reboot / Command+Q
                    System.out.println("System (Windows / OSX / Linux) want to close the app");
                }
            });
        }
    }

## Central Maven Repo

	<dependencies>
		<dependency>
		  <groupId>com.github.axet</groupId>
		  <artifactId>desktop</artifactId>
		  <version>2.0.12</version>
		</dependency>
	</dependencies>
		
