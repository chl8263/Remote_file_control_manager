package client;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import static java.nio.file.FileVisitResult.TERMINATE;

public class MoveFileVisitor implements FileVisitor<Path> {
    private final Path target;
    private final Path source;
    public MoveFileVisitor(@NotNull Path source, @NotNull Path target) {
        this.target = Objects.requireNonNull(target);
        this.source = Objects.requireNonNull(source);
    }
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path relativePath = source.relativize(dir);
        Path finalPath = target.resolve(relativePath);
        Files.createDirectories(finalPath);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        var a = 1;
        Path relativePath = source.relativize(file);
        Path finalLocation = target.resolve(relativePath);
        var moveResult = Files.move(file, finalLocation, StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.out.println("File fail");
        System.out.println(exc.getMessage());
        return TERMINATE;
    }
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
}

