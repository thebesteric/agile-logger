package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.io.*;

/**
 * FileUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class FileUtils {

    public static String read(File file) throws IOException {
        if (file.exists()) {
            String content = read(new FileInputStream(file));
            if (StringUtils.isNotEmpty(content)) {
                return content;
            }
        }
        return null;
    }

    public static String read(InputStream in) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(in)) {
            BufferedReader br = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.length() > 0 ? builder.toString() : null;
        }
    }

}
