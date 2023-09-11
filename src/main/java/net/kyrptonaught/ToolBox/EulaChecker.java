package net.kyrptonaught.ToolBox;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class EulaChecker {
    public static boolean checkEulaAgreement(Path eulaFile) {
        try (InputStream inputStream = Files.newInputStream(eulaFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return Boolean.parseBoolean(properties.getProperty("eula", "false"));
        } catch (Exception exception) {
        }
        return false;
    }

    public static void agreeToEula(Path eulaFile) {
        try (OutputStream outputStream = Files.newOutputStream(eulaFile);) {
            Properties properties = new Properties();
            properties.setProperty("eula", "true");
            properties.store(outputStream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://aka.ms/MinecraftEULA).");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
