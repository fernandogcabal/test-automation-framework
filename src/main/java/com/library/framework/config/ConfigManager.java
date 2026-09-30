package com.library.framework.config;

/*
* ConfigManager centralizes the way the application reads configuration values,
* hiding the configuration source and loading logic from the rest of the framework.
 * */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties PROPERTIES = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    //Executed once when the class is initialized
    static {
        loadProperties();
    }

    //Prevent Objects from being created
    private ConfigManager() {

    }

    // Internal implementation detail
    private static void loadProperties(){
        //TODO: Check this code
        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if(input == null) {
                //TODO: Validate the source of this exception
                throw new IllegalStateException(
                        "Configuration file not found: " + CONFIG_FILE
                );
            }

            PROPERTIES.load(input);
            logger.info("Configuration loaded successfully.");
        }
        catch (IOException e) {
            throw new IllegalStateException(
              "Failed to load configuration: " + CONFIG_FILE, e
            );
        }
    }

    // Give other classes access
    public static String get(String key){
        String systemProperty = System.getProperty(key);

        if (systemProperty != null && !systemProperty.isBlank()){
            return systemProperty.trim();
        }

        //TODO: Understand better PROPERTIES
        return PROPERTIES.getProperty(key);
    }

    // Convenience + validation method
    public static String getRequired(String key){
        String value = get(key);

        if(value == null || value.isBlank()) {
            //TODO: Validate the source of this exception
            throw new IllegalArgumentException(
                    "Required configuration property is missing: " + key
            );
        }
        return value.trim();
    }

    // Convert String to int
    public static int getInt(String key){
        String value = getRequired(key);

        try {
            return Integer.parseInt(value);
        }
        //TODO: Validate the source of this exception
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid integer for property '" + key + "': " +  value, e
            );
        }
    }

    // Convert String to boolean
    public static boolean getBoolean(String key){
        return Boolean.parseBoolean(getRequired(key));
    }
}
