package com.github.assemblathe1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileWatcherTest {

    public static void main(String[] args) {
        File folder = new File("C:\\out");

        FileWatcher watcher = new FileWatcher(folder);

        watcher.addListener(new FileAdapter());

        watcher.watch();
    }

}
