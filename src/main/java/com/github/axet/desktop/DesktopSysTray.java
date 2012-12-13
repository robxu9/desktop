package com.github.axet.desktop;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

public abstract class DesktopSysTray {

    /**
     * OSX dose not have clicks on icon. Just ignore this functionality
     * 
     * @author axet
     * 
     */
    public interface Listener {
        public void mouseLeftClick();

        public void mouseLeftDoubleClick();
    }

    protected Set<Listener> listeners = new HashSet<Listener>();

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    /**
     * show context menu.
     * 
     * OSX dose not support it. Just ignores it.
     */
    public abstract void showContextMenu();

    public abstract void setIcon(Icon icon);

    /**
     * OSX dose not show title on icons.
     * 
     * @param title
     */
    public abstract void setTitle(String title);

    public abstract void show();

    public abstract void update();

    public abstract void hide();

    public abstract void setMenu(JPopupMenu menu);

    public abstract void close();
}
