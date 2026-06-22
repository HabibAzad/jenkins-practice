package com.cydeo.utilities;

import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {

    private static final Properties properties = new Properties();

    static {
        try (var stream = ConfigurationReader.class.getClassLoader()
                              .getResourceAsStream("configuration.properties")) {
            if (stream == null) throw new RuntimeException("configuration.properties not found on classpath");
            properties.load(stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration.properties: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
