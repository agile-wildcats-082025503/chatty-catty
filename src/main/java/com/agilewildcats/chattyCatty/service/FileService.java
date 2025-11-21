package com.agilewildcats.chattyCatty.service;

import com.agilewildcats.chattyCatty.exception.FileServiceException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileService {

    private final Path rootLocation;

    public FileService() {
        this.rootLocation = Paths.get("docs");
    }

    public void store(MultipartFile file) throws FileServiceException {
        try {
            if (file.isEmpty()) {
                throw new FileServiceException("Empty file storage disabled.");
            }
            Path destinationFile = rootLocation
                    .resolve(Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            // Security: Ensure no relative paths are accepted
            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new FileServiceException("Storing files outside current directory is disabled.");
            }
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileServiceException("File storage error:", e);
        }
    }

    public Stream<Path> loadAll() throws FileServiceException {
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .map(rootLocation::relativize);
        } catch (IOException e) {
            throw new FileServiceException("Failed read error:", e);
        }

    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) throws FileServiceException {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileServiceException("Unable to read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileServiceException("Unable to read file: " + filename, e);
        }
    }
}
