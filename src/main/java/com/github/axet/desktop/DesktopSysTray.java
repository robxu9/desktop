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

        // we do not handle right click, because:
        //
        // 1) it is binded to context menu anyway
        //
        // 2) if you call showContextMenu from another java thread, HMENU bugged
        // and you can't use it.
        //
        // 3) Mac OSX dose not support show context menu programmatically
    }

    protected Set<Listener> listeners = new HashSet<Listener>();

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

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
