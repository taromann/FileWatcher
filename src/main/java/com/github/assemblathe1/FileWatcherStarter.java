package com.github.assemblathe1;

import com.github.assemblathe1.listeners.FileAdapter;
import com.github.assemblathe1.utils.DefaultFileFileWatcher;

import java.io.File;
import java.nio.file.Path;

public class FileWatcherTest {

    public static void main(String[] args) {
        FileWatcher watcher = new FileWatcher(Path.of("C:\\out"), new FileAdapter());


        watcher.start();

    }

}
