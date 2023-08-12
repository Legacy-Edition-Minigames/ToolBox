package net.lem.ToolBox;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

public class FileDownloader {

    public static void downloadAndUnzip(String fileURL, String rootPath) {
        Path out = Path.of(rootPath);
        try (InputStream in = new URL(fileURL).openStream();
             SeekableInMemoryByteChannel channel = new SeekableInMemoryByteChannel(IOUtils.toByteArray(in));
             ZipFile zipFile = new ZipFile(channel)) {


            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();

                Path output = out.resolve(entry.getName());
                if (entry.isDirectory())
                    Files.createDirectories(output);
                else
                    Files.copy(zipFile.getInputStream(entry), output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void download(String fileURL, String savePath) {
        // Check if the fileURL starts with a protocol, if not, add "http://" by default
        if (!fileURL.startsWith("http://") && !fileURL.startsWith("https://")) {
            fileURL = "http://" + fileURL;
        }

        Path out = Path.of(savePath);

        try (InputStream in = new URL(fileURL).openStream()) {
            Files.createDirectories(out.getParent());
            Files.copy(in, Path.of(savePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String download(String fileURL) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(fileURL).openStream()))) {
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
}
