package com.github.axet.desktop;

import java.util.HashSet;
import java.util.Set;

public abstract class DesktopPower {

    public interface Listener {
        /**
         * os asks to App to quit.
         * 
         * Windows machines - on reboot / logout. Mac - on reboot / logout +
         * Command + Q Linux - reboot / logout
         */
        public void quit();

    }

    protected Set<Listener> listeners = new HashSet<Listener>();

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    abstract public void close();

}