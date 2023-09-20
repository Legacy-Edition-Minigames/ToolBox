package net.kyrptonaught.ToolBox.holders;

import net.kyrptonaught.ToolBox.ServerRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RunningServer {
    private final AtomicReference<Process> process = new AtomicReference<>();
    private PipedOutputStream stagingPipe;
    private BufferedWriter processInput;

    private final List<String> outputLog = new ArrayList<>();

    private boolean active = false;
    public String stopCMD = "stop";
    public InstalledServerInfo serverInfo;

    public void startServer(InstalledServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        new Thread(() -> {
            try {
                process.set(new ProcessBuilder(serverInfo.getLaunchArgs().split(" "))
                        .directory(new File(System.getProperty("user.dir") + "/" + serverInfo.getPath() + "/"))
                        .redirectErrorStream(true)
                        .start());

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.get().getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLog.add(line);
                    if (active)
                        System.out.println(line);
                }

                reader.close();
                ServerRunner.killServer(this);
            } catch (Exception ignored) {
            }
        }, "Server Instance").start();

        while (process.get() == null) {
            //we have to wait for the process to start
        }

        processInput = new BufferedWriter(new OutputStreamWriter(process.get().getOutputStream()));
    }

    public void setActive() {
        active = true;
        stagingPipe = new PipedOutputStream();
        Thread stagingThread = new Thread(() -> {
            try {
                while (true) {
                    stagingPipe.write(System.in.read());
                }
            } catch (Exception ignored) {
            }
        }, "Server Input Pipe");
        stagingThread.setDaemon(true);
        stagingThread.start();

        for (String line : outputLog) {
            System.out.println(line);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new PipedInputStream(stagingPipe)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("$b")) {
                    setDisabled();
                    return;
                }
                processInput.write(line);
                processInput.newLine();
                processInput.flush();
            }
        } catch (Exception ignored) {
        }
    }

    public boolean isRunning() {
        return process.get() != null;
    }

    public void setDisabled() {
        active = false;
        try {
            stagingPipe.close();
        } catch (Exception ignored) {
        }
    }

    public void stop() {
        //issue shutdown cmd
        try {
            processInput.write(stopCMD);
            processInput.newLine();
            processInput.flush();
        } catch (Exception ignored) {
        }

        //wait 5 seconds
        try {
            Thread.sleep(5000);
        } catch (Exception ignored) {
        }

        //kill
        try {
            kill();
        } catch (Exception ignored) {
        }
    }

    public void kill() {
        setDisabled();
        process.get().destroy();
        process.set(null);
    }
}
