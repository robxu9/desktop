package com.github.axet.desktop.os.mac;

import com.github.axet.desktop.DesktopPower;

public class OSXPower extends DesktopPower {

    AppleHandlers h = new AppleHandlers();

    AppleHandlers.QuitHandler q = new AppleHandlers.QuitHandler() {
        @Override
        public void handleQuit() {
            for (Listener l : listeners) {
                l.quit();
            }
        }
    };

    public OSXPower() {
        h.addQuitHandlerListener(q);
    }

    @Override
    public void close() {
        h.removeQuitHandlerListener(q);
    }

}
