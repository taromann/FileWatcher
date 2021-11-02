package com.github.assemblathe1;

import com.github.assemblathe1.listeners.FileListener;
import com.github.assemblathe1.utils.FileEvent;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher extends SimpleFileVisitor<Path> {

    Set<Path> foldersToWatch = new HashSet<>();
    protected List<FileListener> listeners = new ArrayList<>();
    protected static final List<WatchService> watchServices = new ArrayList<>();

    public FileWatcher(Path folder, FileListener listener) {
        createFoldersTree(folder);
        listeners.add(listener);
    }

    public void start() {
        foldersToWatch.forEach(System.out::println);
        foldersToWatch.forEach(this::addDirectoryToWatching);
    }

    private void createFoldersTree(Path folder) {
        try {
            Files.walkFileTree(folder, this);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        foldersToWatch.add(dir);
        return FileVisitResult.CONTINUE;
    }

    public void addDirectoryToWatching(Path path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
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
        }).start();


    }

    protected boolean pollEvents(WatchService watchService) throws InterruptedException {
//        System.out.println("FileWatcher 66" + Thread.currentThread().getName());
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(event.kind(), path.resolve((Path) event.context()));
        }
        return key.reset();
    }

    protected void notifyListeners(WatchEvent.Kind<?> kind, Path path) {
        FileEvent event = new FileEvent(path);
        if (kind == ENTRY_CREATE) {
            for (FileListener listener : listeners) {
                listener.onCreated(kind, event);
            }
            if (path.toFile().isDirectory()) {
                addDirectoryToWatching(path);
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

//    private void addDirectoryToFileWatcher(Path directory) {
//        listeners.forEach(listener -> );
//
//        new FileWatcher(directory).setListeners(listeners).addDirectoryToWatching(directory);
//    }

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
