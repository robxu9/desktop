package com.github.axet.desktop;

import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

public abstract class DesktopPower {

    public interface Listener {
        /**
         * os asks to App to quit.
         * 
         * Windows machines - on reboot / logout. Mac - on reboot / logout +
         * Command + Q Linux - reboot / logout
         */
        public void quit();

        /**
         * machine goes to sleep
         * 
         * @return false if app want to prevent sleeping (downloading?)
         */
        public boolean sleep();

        /**
         * machine wakeup
         */
        public void wakeup();

        /**
         * machine try to screensaver
         * 
         * i dont know which method i can keep, notify or dirrect command
         * 
         * @return false if app want to prevent screensaver working.
         */
        public boolean screensaver();
    }

    protected Set<Listener> listeners = new HashSet<Listener>();

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    /**
     * prevent screen saver to activate.
     * 
     * i dont know which method i can keep, notify or dirrect command
     */
    abstract public void screensaver();

}