package ToolBox;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java FileDownloader <fileURL> <savePath>");
            return;
        }

        String fileURL = args[0];
        String savePath = args[1];

        try {
            // Check if the fileURL starts with a protocol, if not, add "http://" by default
            if (!fileURL.startsWith("http://") && !fileURL.startsWith("https://")) {
                fileURL = "http://" + fileURL;
            }

            URL url = new URL(fileURL);
            URLConnection connection = url.openConnection();
            BufferedInputStream inStream = new BufferedInputStream(connection.getInputStream());

            // Create the directory structure if it doesn't exist
            new File(new File(savePath).getParent()).mkdirs();

            FileOutputStream fileOutputStream = new FileOutputStream(savePath);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
