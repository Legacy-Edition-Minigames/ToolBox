import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileWorkerThreads {
    public static void RUNCRC(String args) {

        String filePath = args;
        FileProcessor processor = new FileProcessor(filePath);
        processor.processFile();
    }
}

class FileProcessor {
    private final String filePath;

    public FileProcessor(String filePath) {
        this.filePath = filePath;
    }

    public void processFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                Runnable worker = new WorkerThread(line);
                Thread thread = new Thread(worker);
                thread.start();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class WorkerThread implements Runnable {
    private final String content;

    public WorkerThread(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        System.out.println("Thread: " + Thread.currentThread().getId() + " - Content: " + content);
    }
}
