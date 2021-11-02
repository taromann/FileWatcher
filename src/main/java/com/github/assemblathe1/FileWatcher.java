package com.github.assemblathe1;

import com.github.assemblathe1.listeners.FileListener;
import com.github.assemblathe1.utils.FileEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    protected List<FileListener> listeners = new ArrayList<>();
    protected final File folder;
    protected static final List<WatchService> watchServices = new ArrayList<>();

    public FileWatcher(File folder) {
        this.folder = folder;
    }

    public void watch() {
        if (folder.exists()) {
            Thread thread = new Thread(this);
            //thread.setDaemon(true);
            thread.start();
//            System.out.println("FileWarcher: watch() ");
        } else try {
            throw new FileNotFoundException();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
//        System.out.println("FileWarcher: run() ");

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(folder.getAbsolutePath());
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watchServices.add(watchService);
            boolean poll = true;
            while (poll) {
                poll = pollEvents(watchService);
            }
        } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(event.kind(), path.resolve((Path) event.context()).toFile());
        }
        return key.reset();
    }

    protected void notifyListeners(WatchEvent.Kind<?> kind, File file) {
        FileEvent event = new FileEvent(file);
        if (kind == ENTRY_CREATE) {
            for (FileListener listener : listeners) {
                listener.onCreated(kind, event);
            }
            if (file.isDirectory()) {
                addDirectoryToFileWatcher(file);
            }
        }
        else if (kind == ENTRY_MODIFY) {
            for (FileListener listener : listeners) {
                listener.onModified(kind, event);
            }
        } else if (kind == ENTRY_DELETE) {
            for (FileListener listener : listeners) {
                listener.onDeleted(kind, event);
            }
        }
    }

    private void addDirectoryToFileWatcher(File file) {
        new FileWatcher(file).setListeners(listeners).watch();
    }

    public FileWatcher addListener(FileListener listener) {
        listeners.add(listener);
        return this;
    }

    public FileWatcher removeListener(FileListener listener) {
        listeners.remove(listener);
        return this;
    }

    public List<FileListener> getListeners() {
        return listeners;
    }

    public FileWatcher setListeners(List<FileListener> listeners) {
        this.listeners = listeners;
        return this;
    }

    public static List<WatchService> getWatchServices() {
        return Collections.unmodifiableList(watchServices);
    }
}
