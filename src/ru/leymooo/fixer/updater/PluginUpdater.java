package ru.leymooo.fixer.updater;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents the updater of Bukkit plugin from GitHub commits.
 *
 * @author CatCoder
 */
public class PluginUpdater {

    //Base GitHubAPI URL
    public static final String BASE_URL = "https://api.github.com/";
    //Using for parsing input responses
    public static final Gson GSON = new Gson();

    //Target plugin
    private final Plugin plugin;
    //Plugin version
    private int currentVersion;
    //GitHub repo
    private final String repositoryUrl;

    public PluginUpdater(Plugin plugin, String user, String repository) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be NULL.");
        this.plugin = plugin;
        this.repositoryUrl = "/".concat(user.concat("/").concat(repository).concat("/"));
    }

    /**
     * Check for new updates by GitHub API.
     *
     * @return - result of checking (UPDATE_FOUND, UPDATE_NOT_FOUND)
     * @throws UpdaterException - for any unhandled exceptions.
     */
    public UpdaterResult checkUpdates() throws UpdaterException {
        if (this.currentVersion == 0) currentVersion = parseVersion();
        try {
            //Connect to GitHub API.
            URL url = new URL(BASE_URL.concat("repos").concat(repositoryUrl).concat("commits"));
            //Parse JSON.
            JsonObject[] objects = GSON.fromJson(new BufferedReader(
                            new InputStreamReader(
                                    url.openStream(),
                                    Charsets.UTF_8.name())),
                    JsonObject[].class);
            if (objects.length == 0) {
                throw new UpdaterException("commits is empty", this);
            }
            //Get the first object in array.
            JsonObject object = objects[0];
            //Extract the version from commit message.
            int version = Integer.parseInt(object.get("commit").getAsJsonObject().get("message").getAsString().replace(".", ""));
            //Compare current version with remote version.
            if (version > currentVersion) {
                //Yay, we found a new update!
                return UpdaterResult.UPDATE_FOUND;
            }
        } catch (MalformedURLException e) {
            throw new UpdaterException(e.getMessage(), this);
        } catch (IOException e) {
            throw new UpdaterException("unhandled error " + e.getMessage(), this);
        } catch (NumberFormatException ignore) {
            //Ignore it.
        }
        //Updates not found :(
        return UpdaterResult.UPDATE_NOT_FOUND;
    }

    /**
     * Parse local version.
     *
     * @return parsed double number.
     */
    private int parseVersion() throws UpdaterException {
        try {
            return Integer.parseInt(plugin.getDescription().getVersion().replace(".", ""));
        } catch (NumberFormatException ex) {
            throw new UpdaterException("cannot parse version " + ex.getMessage(), this);
        }
    }

    /**
     * Target plugin.
     *
     * @return plugin.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Current version of plugin.
     *
     * @return - version number
     */
    public int getCurrentVersion() {
        return currentVersion;
    }
}
