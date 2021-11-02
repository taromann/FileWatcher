package com.github.assemblathe1;

import com.github.assemblathe1.listeners.FileAdapter;
import com.github.assemblathe1.utils.DefaultFileFileWatcher;

import java.io.File;

public class FileWatcherTest {

    public static void main(String[] args) {
        File folder = new File("C:\\out");

        DefaultFileFileWatcher defaultFileFileWatcher = new DefaultFileFileWatcher(folder.toPath());

        defaultFileFileWatcher.getDefaultPathsToWatch().forEach(path -> new FileWatcher(path.toFile()).addListener(new FileAdapter()).watch());


        FileWatcher watcher = new FileWatcher(folder);

        watcher.addListener(new FileAdapter());

        watcher.watch();
    }

}
