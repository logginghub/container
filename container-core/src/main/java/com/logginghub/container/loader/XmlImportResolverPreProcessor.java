package com.logginghub.container.loader;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Looks for <importContainer>containerFileLocation</importContainer> and expands the contents
 * of that file (and any recursively referenced file imports).
 *
 * Currently the file location will be treated as a System Resource, so should be referenced
 * in the context of the classpath.
 *
 * @author cspiking
 */
public class XmlImportResolverPreProcessor implements PreProcessor {

    private static final String IMPORT_CONTAINER_START = "<importContainerFile>";
    private static final String IMPORT_CONTAINER_END = "</importContainerFile>";
    private static final String CONTAINER_START = "<container>";
    private static final String CONTAINER_END = "</container>";

    private static final String PATTERN_STRING =
            IMPORT_CONTAINER_START + "(.*)" + IMPORT_CONTAINER_END;

    private final Pattern pattern = Pattern.compile(PATTERN_STRING);

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
        Matcher matcher = pattern.matcher(resolvedStringSoFar);
        while(matcher.find()) {
            final InputStream importedFileAsStream = ClassLoader.getSystemResourceAsStream(matcher.group(1));
            final String processedImportedFileString =
                    preProcessFromInputStreamToString(importedFileAsStream, true);
            resolvedStringSoFar = matcher.replaceFirst(processedImportedFileString);
            matcher = pattern.matcher(resolvedStringSoFar);
        }
        return resolvedStringSoFar;
    }

    private String stripContainerTag(String inputStreamAsString) {
        String updatedString = inputStreamAsString.replaceAll(CONTAINER_START, "");
        updatedString = updatedString.replaceAll(CONTAINER_END, "");
        return updatedString;
    }

}
