package org.example.firsthomework.session;

import java.io.InputStream;
import java.util.Properties;

public class PropertyFile {
    private final Properties properties;

    public PropertyFile(String propertyPath) {
        this.properties = new Properties();
        try (InputStream file = getClass().getClassLoader().getResourceAsStream(propertyPath)) {
            properties.load(file);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }
}
