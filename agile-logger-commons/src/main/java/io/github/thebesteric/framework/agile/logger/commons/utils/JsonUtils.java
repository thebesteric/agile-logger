package io.github.thebesteric.framework.agile.logger.commons.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JsonUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 12:17:15
 */
public class JsonUtils {

    private static final Pattern PATTERN = Pattern.compile("\\s*|\t|\r|\n");

    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    public static String toJsonStr(String str) {
        Matcher matcher = PATTERN.matcher(str);
        str = matcher.replaceAll("");
        str = str.replaceAll("\"(\\w+)\"", "$1");
        str = str.replace("\"", "");
        str = str.replace("'", "");
        str = str.replace(":", "\":\"");
        str = str.replace(",", "\",\"");
        str = str.replace("[", "[\"");
        str = str.replace("]", "\"]");
        str = str.replace("\"[", "[");
        str = str.replace("]\"", "]");
        str = str.replace("{", "{\"");
        str = str.replace("}", "\"}");
        str = str.replace("\"{", "{");
        str = str.replace("}\"", "}");
        str = str.replace("[\"{]", "[{");
        str = str.replace("}\"]", "}]");
        str = str.replace("{\"[", "{[");
        str = str.replace("]\"}", "]}");
        str = str.replace("http\":\"", "http:");
        str = str.replace("https\":\"", "https:");
        str = str.replace("ftp\":\"", "ftp:");
        return str;
    }

}
