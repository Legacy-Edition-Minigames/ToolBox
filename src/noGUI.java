import java.io.IOException;
import java.util.Objects;

public class noGUI {

    public static void GUI() {
        try {
            Conf.createIfNotExists();
            FileDownloader.main(new String[]{Conf.get(2) + "/" + Conf.get(0) + "/main.ok", "./main.ok"});

            String confValue = ReadSpecificLine.main(new String[]{"./main.ok", "1"});
            if (!Objects.equals(Conf.get(1), confValue)) {
                Conf.set("1", confValue);
                OUTKAT.executeScript("./main.ok");
            }

            FileDownloader.main(new String[]{Conf.get(2) + "/" + Conf.get(0) + "/CRC", "./CRC"});
            FileWorkerThreads.RUNCRC("./CRC");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception here, if needed
        }
    }
}
