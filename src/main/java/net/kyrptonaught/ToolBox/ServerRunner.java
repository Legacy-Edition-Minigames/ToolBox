package net.kyrptonaught.ToolBox;


import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

public class ServerRunner {

    public static void runServer(InstalledServerInfo serverInfo) {
        AtomicReference<Process> process = new AtomicReference<>();
        PipedOutputStream stagingPipe = new PipedOutputStream();

        Thread stagingThread = new Thread(() -> {
            try {
                while (true) {
                    stagingPipe.write(System.in.read());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Server Input Pipe");
        stagingThread.setDaemon(true);
        stagingThread.start();

        new Thread(() -> {
            try {
                process.set(new ProcessBuilder(serverInfo.getLaunchArgs().split(" "))
                        .directory(new File(System.getProperty("user.dir") + "/" + serverInfo.getPath() + "/"))
                        .redirectErrorStream(true)
                        .start());
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.get().getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                reader.close();
                stagingPipe.close();
                stagingThread.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Server Instance").start();

        while (process.get() == null) {
            //we have to wait for the process to start
        }

        try (PipedInputStream pipe = new PipedInputStream(stagingPipe);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.get().getOutputStream()))) {
            int line;
            while ((line = pipe.read()) != -1) {
                writer.write(line);
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
