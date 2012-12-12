package com.github.axet.desktop;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

public abstract class DesktopSysTray {

    public interface Listener {
        public void mouseLeftClick();

        public void mouseLeftDoubleClick();

        public void mouseRightClick();
    }

    protected Set<Listener> listeners = new HashSet<Listener>();

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    public abstract void showContextMenu();

    public abstract void setIcon(Icon icon);

    public abstract void setTitle(String title);

    public abstract void show();

    public abstract void update();

    public abstract void hide();

    public abstract void setMenu(JPopupMenu menu);

    public abstract void close();
}
