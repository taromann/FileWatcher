package com.github.assemblathe1.listeners;

import com.github.assemblathe1.utils.FileEvent;

import java.nio.file.WatchEvent;
import java.util.EventListener;

public interface FileListener extends EventListener {
    public void onCreated(WatchEvent.Kind<?> kind, FileEvent event);
    public void onModified(WatchEvent.Kind<?> kind, FileEvent event);
    public void onDeleted(WatchEvent.Kind<?> kind, FileEvent event);
}