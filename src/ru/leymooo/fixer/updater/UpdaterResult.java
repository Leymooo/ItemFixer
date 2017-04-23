package ru.leymooo.fixer.updater;

/**
 * Created by Ruslan on 23.04.2017.
 */
public enum UpdaterResult {

    UPDATE_FOUND,
    UPDATE_NOT_FOUND;

    public boolean hasUpdates() {
        return this.equals(UPDATE_FOUND);
    }
}
