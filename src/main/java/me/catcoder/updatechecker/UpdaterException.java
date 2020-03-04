package me.catcoder.updatechecker;

import java.util.logging.Level;

/**
 * Created by Ruslan on 23.04.2017.
 */
public class UpdaterException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7371026016860990753L;
    private final String message;
    private final PluginUpdater updater;

    public UpdaterException(String message, PluginUpdater updater) {
        super(message);
        this.message = message;
        this.updater = updater;
    }

    public void print() {
        updater.getPlugin().getLogger().log(Level.SEVERE, String.format(
                printableMessage(),
                updater.getPlugin().getName(),
                message));
    }


    public String printableMessage() {
        return "[PluginUpdater]: Error of plugin %s (%s)";
    }
}
