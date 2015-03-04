package com.logginghub.container.loader;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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

    @Override
    public InputStream preProcessFromInputStreamToInputStream(InputStream inputStream) {
        try {
            return new ByteArrayInputStream(preProcessFromInputStreamToString(inputStream, false).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String preProcessFromInputStreamToString(InputStream inputStream, boolean stripContainerTag) throws IOException {
        final StringBuilder returnedString = new StringBuilder();
        String inputStreamAsString = IOUtils.toString(inputStream, Charset.defaultCharset());

        if(stripContainerTag) {
            inputStreamAsString = stripContainerTag(inputStreamAsString);
        }

        int indexOfImportContainerStart = inputStreamAsString.indexOf(IMPORT_CONTAINER_START);
        if(indexOfImportContainerStart == -1) {
            returnedString.append(inputStreamAsString);
        } else {
            String unprocessedString = inputStreamAsString;

            while(indexOfImportContainerStart != -1) {
                final int indexOfImportContainerEnd = unprocessedString.indexOf(IMPORT_CONTAINER_END);
                if(indexOfImportContainerEnd == -1) {
                    throw new RuntimeException("Closing tag " + IMPORT_CONTAINER_END + " not found.");
                }

                final String contentsUntilImportTag = unprocessedString.substring(0, indexOfImportContainerStart);
                returnedString.append(contentsUntilImportTag);

                final String importFileName =
                        unprocessedString.substring(
                                indexOfImportContainerStart + IMPORT_CONTAINER_START.length(),
                                indexOfImportContainerEnd);
                final String importedFileAsString =
                        preProcessFromInputStreamToString(
                                ClassLoader.getSystemResourceAsStream(importFileName), true);
                returnedString.append(importedFileAsString);

                unprocessedString = unprocessedString.substring(
                        indexOfImportContainerEnd + IMPORT_CONTAINER_END.length());
                indexOfImportContainerStart = unprocessedString.indexOf(IMPORT_CONTAINER_START);
            }
            returnedString.append(unprocessedString);
        }
        return returnedString.toString();
    }

    private String stripContainerTag(String inputStreamAsString) {
        final int indexOfContainerTag = inputStreamAsString.indexOf(CONTAINER_START);
        if(indexOfContainerTag == -1) {
            return inputStreamAsString;
        } else {
            final int indexOfContainerEndTag = inputStreamAsString.indexOf(CONTAINER_END);
            if(indexOfContainerEndTag == -1) {
                throw new RuntimeException("End container tag " + CONTAINER_END + " not found");
            }
            return inputStreamAsString.substring(
                    indexOfContainerTag + CONTAINER_START.length(),
                    indexOfContainerEndTag);
        }
    }

}
