import java.io.IOException;
import java.util.Objects;

public class noGUI {

    public static void GUI() throws IOException {
        Conf.createIfNotExists();
        FileDownloader.main(new String[]{Conf.get(2) + "/" + Conf.get(0) + "/main.ok", "./main.ok"});
        if (Objects.equals(Conf.get(1), ReadSpecificLine.main(new String[]{"./main.ok", "1"}))){
            Conf.set("1", ReadSpecificLine.main(new String[]{"./main.ok", "1"}));
            OUTKAT.executeScript("./main.ok");
        }
        FileDownloader.main(new String[]{Conf.get(2) + "/" + Conf.get(0) + "/CRC", "./CRC"});
        FileWorkerThreads.RUNCRC("./CRC");
    }

}