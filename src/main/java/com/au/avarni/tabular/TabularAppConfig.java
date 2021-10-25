package com.au.avarni.tabular;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class TabularAppConfig {

    private static Map<String, String> config = null;
    private static final String propertyPrefix = "tabularApp.";

    /**
     * Returns the key-value pairs stored inside resources/config.properties.
     *
     * @return Key-value pairs from resources/config.properties
     */
    public static Map<String, String> getAppConfig() throws Exception {
        // Return existing config if already loaded before
        if (config != null) {
            return config;
        }

        Properties properties = new Properties();

        String propFileName = "config.properties";

        InputStream inputStream = TabularAppUtils.class.getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        config = properties
                .entrySet()
                .stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().toString(), e.getValue().toString()))
                .filter(e -> e.getKey().contains(propertyPrefix))
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().replaceFirst(propertyPrefix, ""), e.getValue()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        return config;
    }
}
