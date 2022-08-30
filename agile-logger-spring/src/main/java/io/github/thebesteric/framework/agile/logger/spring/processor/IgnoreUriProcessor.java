package io.github.thebesteric.framework.agile.logger.spring.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IgnoreUrlProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface IgnoreUriProcessor {
    Set<String> IGNORE_URIS = new HashSet<>(16);

    /**
     * Add ignore uris
     *
     * @param ignoreUrls ignoreUrls
     */
    void add(Set<String> ignoreUrls);

    /**
     * Get IGNORE_URLS
     *
     * @return {@link Set<String>}
     */
    default Set<String> get() {
        return IGNORE_URIS;
    }


    /**
     * Whether the value matching the regular
     *
     * @param url String
     * @return boolean
     */
    default boolean matching(String url) {
        for (String ignoreUri : IGNORE_URIS) {
            Matcher matcher = Pattern.compile(ignoreUri).matcher(url);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    default void addDefaultIgnoreUris() {
        IGNORE_URIS.add("/favicon.ico");
    }

}
