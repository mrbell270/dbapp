package delta.database;

/**
 * Created by lenovo on 16.02.2017.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Context {
        private static Context instance;
        private static final Map<String, String> properties = new HashMap<String, String>();
        private static InputStream inputStream;

        private void readPropertiesDatabaseConfigFile(Properties prop) {
            properties.put("DB_URL", prop.getProperty("DB_URL"));
            properties.put("DB_USER", prop.getProperty("DB_USER"));
            properties.put("DB_PASS", prop.getProperty("DB_PASS"));
        }

        private void readFromConfigFile() throws IOException {
            try {
                Properties prop = new Properties();

                inputStream = getClass().getClassLoader().getResourceAsStream("DB.properties");

                if (inputStream != null) {
                    prop.load(inputStream);
                } else {
                    throw new FileNotFoundException("Property file 'DB.properties' not found in the classpath");
                }
                readPropertiesDatabaseConfigFile(prop);

            } catch (Exception e) {

            } finally {
                inputStream.close();
            }
        }

        private Context() {
            try {
                readFromConfigFile();
            } catch (Exception e) {

            }
        }

        public static synchronized String getPropertyByName(String name) {
            if (instance == null) {
                instance = new Context();
            }
            return properties.get(name);
        }
}
