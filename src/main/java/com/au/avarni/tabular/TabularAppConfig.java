package com.au.avarni.tabular;

import com.au.avarni.tabular.objects.TabularConfigObject;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.InputStream;

public class TabularAppConfig {

    private static TabularConfigObject config = null;

    /**
     * Returns the key-value pairs stored inside config/tabularConfig.yml.
     *
     * @return Key-value pairs from config/tabularConfig.yml
     */
    public static TabularConfigObject getAppConfig() throws Exception {
        // Return existing config if already loaded before
        if (config != null) {
            return config;
        }

        InputStream inputStream = new FileInputStream("config/tabularConfig.yml");
        Yaml yaml = new Yaml(new Constructor(TabularConfigObject.class));
        config = yaml.load(inputStream);

        return config;
    }
}
