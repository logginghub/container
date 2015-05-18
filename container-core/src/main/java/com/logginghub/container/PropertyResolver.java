package com.logginghub.container;

import java.util.Properties;

/**
 * Created by james on 18/05/2015.
 */
public interface PropertyResolver {
    String resolve(String string);


    class SystemPropertyResolver implements PropertyResolver {
        @Override
        public String resolve(String string) {
            return System.getProperty(string);
        }
    }

    class EnvironmentPropertyResolver implements PropertyResolver {
        @Override
        public String resolve(String string) {
            return System.getenv(string);
        }
    }

    class PropertiesPropertyResolver implements PropertyResolver {
        private final Properties properties;

        public PropertiesPropertyResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolve(String string) {
            return properties.getProperty(string);
        }
    }

}
