package com.ucc.oegs;

/**
 * Plain (non-JavaFX) entry point.
 *
 * <p>When an application's main class extends {@link javafx.application.Application},
 * launching it directly from a non-modular class path triggers the JVM's
 * "JavaFX runtime components are missing" guard. Delegating {@code main} to this
 * separate launcher — which does <em>not</em> extend {@code Application} — is the
 * standard workaround and keeps {@code mvn javafx:run} and plain {@code java}
 * launches working alike.</p>
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        OegsApp.main(args);
    }
}
