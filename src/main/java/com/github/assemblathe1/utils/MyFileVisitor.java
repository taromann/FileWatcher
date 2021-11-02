package com.github.assemblathe1.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class MyFileVisitor extends SimpleFileVisitor<Path> {

    Set<Path> defaultPathsToWatch = new HashSet<>();

    public Set<Path> getDefaultPathsToWatch() {
        return defaultPathsToWatch;
    }


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        defaultPathsToWatch.add(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        defaultPathsToWatch.add(dir);
        return FileVisitResult.CONTINUE;
    }

    public void soutDirectories() {
        defaultPathsToWatch.stream().map(Path::toString).forEach(System.out::println);
    }
}
