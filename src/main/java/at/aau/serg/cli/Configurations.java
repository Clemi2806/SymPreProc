package at.aau.serg.cli;

import com.google.common.io.Files;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Properties;

public class Configurations {
    public static Configurations INSTANCE;

    private final Properties properties = new Properties();

    private Configurations(Path configurationPath) throws IOException {
        properties.load(Files.newReader(configurationPath.toFile(), Charset.defaultCharset()));
    }

    public String getPropertyAsString(String key) {
        return properties.getProperty(key);
    }

    public String[] getPropertyAsStringArray(String key) {
        if (!properties.containsKey(key) || properties.getProperty(key).isEmpty()) {
            return new String[0];
        }
        return properties.getProperty(key).split("\\s*,\\s*");
    }

    public static Configurations getInstance(Path configurationPath) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new Configurations(configurationPath);
        }
        return INSTANCE;
    }

    public static Configurations getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Configurations not initialized yet");
        }
        return INSTANCE;
    }

    public static boolean exists() {
        return INSTANCE != null;
    }

    public static void reset() {
        INSTANCE = null;
    }
}
