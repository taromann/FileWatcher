package com.github.assemblathe1;

import com.github.assemblathe1.listeners.FileAdapter;

import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchService;

public class FileWatcherStarter {

    public static void main(String[] args) {
        FileWatcher watcher = new FileWatcher(Path.of("C:\\out"), new FileAdapter());
        watcher.start();
    }
}
