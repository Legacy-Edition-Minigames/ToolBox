package net.kyrptonaught.ToolBox;

import net.kyrptonaught.ToolBox.configs.BranchConfig;

import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

public class ServerRunner {

    public static void runServer(BranchConfig branch) {
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
                process.set(new ProcessBuilder(branch.launchCMD.split(" "))
                        .directory(new File(System.getProperty("user.dir") + "/" + Paths.getInstallPath() + "/"))
                        .redirectErrorStream(true)
                        .start());
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.get().getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Server: " + line);
                }

                reader.close();
                stagingPipe.close();
                stagingThread.stop();//todo this doesn't actually stop the thread. Will be an issue if another server launches before toolbox closes
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
