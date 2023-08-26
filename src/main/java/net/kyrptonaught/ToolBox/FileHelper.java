package net.kyrptonaught.ToolBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

public class FileHelper {

    public static boolean download(String fileURL, Path savePath) {
        try (InputStream in = openFileOrURL(fileURL)) {
            Files.createDirectories(savePath.getParent());
            Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String download(String fileURL) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openFileOrURL(fileURL)))) {
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null)
                response.append(inputLine);

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T download(String fileURL, Class<T> clazz) {
        return ConfigLoader.gson.fromJson(download(fileURL), clazz);
    }

    public static boolean unzipFile(Path zipFile, Path unzipPath) {
        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(zipFile.toFile())) {
            int size = zip.size();
            int count = 0;


            boolean firstChecked = false;
            String initialDir = null;

            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (!firstChecked) {
                    firstChecked = true;
                    if (entry.isDirectory())
                        initialDir = entry.getName();
                }

                Path output = unzipPath.resolve(initialDir == null ? entry.getName() : entry.getName().replace(initialDir, ""));
                if (entry.isDirectory())
                    Files.createDirectories(output);
                else
                    Files.copy(zip.getInputStream(entry), output, StandardCopyOption.REPLACE_EXISTING);
                count++;
                //System.out.println(count + "/" + size);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static InputStream openFileOrURL(String path) throws IOException {
        path = path.trim();
        if (path.startsWith("file:"))
            return Files.newInputStream(Path.of(path.replace("file:", "")), StandardOpenOption.READ);

        return new URL(path).openStream();
    }

    public static boolean createDir(Path directory) {
        try {
            Files.createDirectories(directory);
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to create directory: " + directory);
            exception.printStackTrace();
        }
        return false;
    }

    public static boolean moveFile(Path source, Path destination) {
        try {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to copy file: " + source + " -> " + destination);
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteDirectory(Path directory) {
        try {
            Files.walkFileTree(directory,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(
                                Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String hashFile(Path filePath) {
        try {
            byte[] data = Files.readAllBytes(filePath);
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (Exception e) {
            System.out.println("Failed to hash file: " + filePath);
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeHash(Path filePath, String hash) {
        try {
            Files.writeString(filePath, hash);
            return true;
        } catch (Exception e) {
            System.out.println("Error writing hash: " + filePath);
            e.printStackTrace();
        }
        return false;
    }

    public static String readHash(Path filePath) {
        try {
            return Files.readString(filePath);
        } catch (Exception e) {
            System.out.println("Error reading hash: " + filePath);
            e.printStackTrace();
        }
        return null;
    }
}
