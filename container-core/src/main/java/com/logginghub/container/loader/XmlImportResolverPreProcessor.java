package com.logginghub.container.loader;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Looks for {@code <importContainer>containerFileLocation</importContainer> } and expands the contents
 * of that file (and any recursively referenced file imports).
 *
 * Currently the file location will be treated as a System Resource, so should be referenced
 * in the context of the classpath.
 *
 * @author cspiking
 */
public class XmlImportResolverPreProcessor implements PreProcessor {

    private static final String CONTAINER_START = "<container>";
    private static final String CONTAINER_END = "</container>";

    private static final String LONG_IMPORT_PATTERN_STRING =
            "<importContainerFile>(.*)</importContainerFile>";

    private static final String SHORT_IMPORT_PATTERN_STRING =
            "<importContainerFile\\s*file=[\"'](.*)[\"']\\s*/>";

    private final Pattern longImportPattern = Pattern.compile(LONG_IMPORT_PATTERN_STRING);
    private final Pattern shortImportPattern = Pattern.compile(SHORT_IMPORT_PATTERN_STRING);

    @Override
    public InputStream preProcessFromInputStreamToInputStream(InputStream inputStream) {
        try {
            return new ByteArrayInputStream(preProcessFromInputStreamToString(inputStream, false).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String preProcessFromInputStreamToString(InputStream inputStream, boolean stripContainerTag) throws IOException {
        String inputStreamAsString = IOUtils.toString(inputStream, Charset.defaultCharset());
        if(stripContainerTag) {
            inputStreamAsString = stripContainerTag(inputStreamAsString);
        }

        String resolvedStringSoFar = inputStreamAsString;

        Matcher longFormatMatcher = longImportPattern.matcher(resolvedStringSoFar);
        Matcher shortFormatMatcher = shortImportPattern.matcher(resolvedStringSoFar);
        boolean longFormatFound = longFormatMatcher.find();
        boolean shortFormatFound = shortFormatMatcher.find();

        while(longFormatFound || shortFormatFound) {
            final Matcher effectiveMatcher = longFormatFound ? longFormatMatcher : shortFormatMatcher;
            final InputStream importedFileAsStream =
                    ClassLoader.getSystemResourceAsStream(effectiveMatcher.group(1));
            final String processedImportedFileString =
                    preProcessFromInputStreamToString(importedFileAsStream, true);
            resolvedStringSoFar = effectiveMatcher.replaceFirst(processedImportedFileString);

            longFormatMatcher = longImportPattern.matcher(resolvedStringSoFar);
            shortFormatMatcher = shortImportPattern.matcher(resolvedStringSoFar);
            longFormatFound = longFormatMatcher.find();
            shortFormatFound = shortFormatMatcher.find();
        }
        return resolvedStringSoFar;
    }

    private String stripContainerTag(String inputStreamAsString) {
        String updatedString = inputStreamAsString.replaceAll(CONTAINER_START, "");
        updatedString = updatedString.replaceAll(CONTAINER_END, "");
        return updatedString;
    }

}
