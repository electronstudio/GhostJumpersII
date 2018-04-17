package uk.co.electronstudio.ghostjumpers;

import uk.me.fantastic.retro.Logger;

public class IOSLogger implements Logger {
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

    @Override
    public void error(String s) {
        log(s);
    }
}
