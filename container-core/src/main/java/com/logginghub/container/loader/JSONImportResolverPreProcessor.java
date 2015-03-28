package com.logginghub.container.loader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 * @author cspiking
 */
public class JSONImportResolverPreProcessor implements PreProcessor {

    private static final String CONTAINER_OBJECT_ID = "container";
    private static final String IMPORT_OBJECT_ID = "importJsonContainerFile";

    private final Gson gson = new Gson();

    @Override
    public InputStream preProcessFromInputStreamToInputStream(InputStream inputStream) {
        try {
            final JsonObject processedObjectToReturn = createNewContainerJsonObject();

            final JsonArray arrayOfProcessedObjects = preProcessFromInputStreamToJsonObject(inputStream);
            processedObjectToReturn.add(CONTAINER_OBJECT_ID, arrayOfProcessedObjects);

            final String containerObjectJsonString = gson.toJson(processedObjectToReturn);
            return new ByteArrayInputStream(containerObjectJsonString.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonArray preProcessFromInputStreamToJsonObject(InputStream inputStream) throws IOException {
        final String inputStreamAsJsonString = IOUtils.toString(inputStream, Charset.defaultCharset());
        return preProcessFromStringToString(inputStreamAsJsonString);
    }

    private JsonArray preProcessFromStringToString(String jsonString) throws IOException {
        final JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        final JsonArray containerObjectArray = jsonObject.getAsJsonArray(CONTAINER_OBJECT_ID);
        final JsonArray destinationObjectArray = new JsonArray();
        if(containerObjectArray != null) {
            for(JsonElement sourceJsonElement : containerObjectArray) {
                if(sourceJsonElement.isJsonObject()) {
                    JsonElement importReferenceElement = sourceJsonElement.getAsJsonObject().get(IMPORT_OBJECT_ID);
                    if(importReferenceElement != null && importReferenceElement.isJsonPrimitive()) {
                        final String importFileName = importReferenceElement.getAsString();
                        final JsonArray importObjectArray = preProcessFromFileName(importFileName);
                        destinationObjectArray.addAll(importObjectArray);
                    } else {
                        destinationObjectArray.add(sourceJsonElement);
                    }
                } else {
                    destinationObjectArray.add(sourceJsonElement);
                }
            }
        } else {
            throw new IllegalArgumentException("Json input does not contain 'container' object : " + jsonString);
        }
        return destinationObjectArray;
    }

    private final JsonArray preProcessFromFileName(String fileName) throws IOException {
        final InputStream importedFileAsStream = ClassLoader.getSystemResourceAsStream(fileName);
        return preProcessFromInputStreamToJsonObject(importedFileAsStream);
    }

    private JsonObject createNewContainerJsonObject() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add(CONTAINER_OBJECT_ID, new JsonArray());
        return jsonObject;
    }
}
