package uk.co.electronstudio.ghostjumpers;

import uk.me.fantastic.retro.RLogger;

public class IOSLogger implements RLogger {
    @Override
    public void log(String s) {
        System.out.println(s);
    }

    @Override
    public void log(String s, String s1) {
        log(s+" "+s1);

    }

    @Override
    public void initialize() {

    }
}
