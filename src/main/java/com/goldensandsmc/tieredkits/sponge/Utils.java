package com.goldensandsmc.tieredkits.sponge;

public class Utils {
    public Utils() {
    }

    public static Throwable getRootCause(Throwable thrown) {
        while(thrown.getCause() != null) {
            thrown = thrown.getCause();
        }

        return thrown;
    }
}
