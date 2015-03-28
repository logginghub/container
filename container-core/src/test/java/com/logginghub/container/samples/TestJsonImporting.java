package com.logginghub.container.samples;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.logginghub.container.loader.JSONImportResolverPreProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 * Created by chrisspikingdev on 24/03/2015.
 */
public class TestJsonImporting {

    @Test
    public void testSimpleOneFileImportAggregatesAllElementsIntoOneContainer() throws Exception {
        final JSONImportResolverPreProcessor jsonImportResolverPreProcessor = new JSONImportResolverPreProcessor();

        final InputStream testFile = ClassLoader.getSystemResourceAsStream("samples/json_import_1_input1.json");
        final InputStream resultInputStream = jsonImportResolverPreProcessor.preProcessFromInputStreamToInputStream(testFile);
        final String processedObjectAsString = IOUtils.toString(resultInputStream, Charset.defaultCharset());

        final InputStream expectedResultFile = ClassLoader.getSystemResourceAsStream("samples/json_import_1_expectedOutput.json");
        final String expectedObjectAsString = IOUtils.toString(expectedResultFile, Charset.defaultCharset());

        final Gson gson = new Gson();
        final JsonObject processedObject = gson.fromJson(processedObjectAsString, JsonObject.class);
        final JsonObject expectedObject = gson.fromJson(expectedObjectAsString, JsonObject.class);

        assertEquals(gson.toJson(expectedObject), gson.toJson(processedObject));
    }

    @Test
    public void testImportOfTwoFilesInOneFileAggregatesAllElements() throws Exception {
        final JSONImportResolverPreProcessor jsonImportResolverPreProcessor = new JSONImportResolverPreProcessor();

        final InputStream testFile = ClassLoader.getSystemResourceAsStream("samples/json_import_2_input1.json");
        final InputStream resultInputStream = jsonImportResolverPreProcessor.preProcessFromInputStreamToInputStream(testFile);
        final String processedObjectAsString = IOUtils.toString(resultInputStream, Charset.defaultCharset());

        final InputStream expectedResultFile = ClassLoader.getSystemResourceAsStream("samples/json_import_2_expectedOutput.json");
        final String expectedObjectAsString = IOUtils.toString(expectedResultFile, Charset.defaultCharset());

        final Gson gson = new Gson();
        final JsonObject processedObject = gson.fromJson(processedObjectAsString, JsonObject.class);
        final JsonObject expectedObject = gson.fromJson(expectedObjectAsString, JsonObject.class);

        assertEquals(gson.toJson(expectedObject), gson.toJson(processedObject));
    }

    @Test
    public void testNestedImportOfTwoFilesAggregatesIntoOneFile() throws Exception {
        final JSONImportResolverPreProcessor jsonImportResolverPreProcessor = new JSONImportResolverPreProcessor();

        final InputStream testFile = ClassLoader.getSystemResourceAsStream("samples/json_import_3_input1.json");
        final InputStream resultInputStream = jsonImportResolverPreProcessor.preProcessFromInputStreamToInputStream(testFile);
        final String processedObjectAsString = IOUtils.toString(resultInputStream, Charset.defaultCharset());

        final InputStream expectedResultFile = ClassLoader.getSystemResourceAsStream("samples/json_import_3_expectedOutput.json");
        final String expectedObjectAsString = IOUtils.toString(expectedResultFile, Charset.defaultCharset());

        final Gson gson = new Gson();
        final JsonObject processedObject = gson.fromJson(processedObjectAsString, JsonObject.class);
        final JsonObject expectedObject = gson.fromJson(expectedObjectAsString, JsonObject.class);

        assertEquals(gson.toJson(expectedObject), gson.toJson(processedObject));
    }
}
