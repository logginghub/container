package com.logginghub.container;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

/**
 * Created by james on 10/02/15.
 */
public class ContainerJSONLoader extends ContainerLoaderBase {

    public Container loadFromResource(String string) {
        return loadFromStream(ClassLoader.getSystemResourceAsStream(string));
    }

    public Container loadFromString(String string) {
        return loadFromStream(new ByteArrayInputStream(string.getBytes()));
    }

    public Container loadFromStream(InputStream is) {

        JsonParser parser = new JsonParser();
        JsonObject containerObject = parser.parse(new InputStreamReader(is)).getAsJsonObject();

        Container container = new Container();

        final JsonArray modules = containerObject.getAsJsonArray("container");
        for (JsonElement moduleElement : modules) {

            if (moduleElement.isJsonObject()) {
                final JsonObject moduleAsJsonObject = moduleElement.getAsJsonObject();

                final Set<Map.Entry<String, JsonElement>> entries = moduleAsJsonObject.entrySet();
                if (entries.size() == 1) {
                    Map.Entry<String, JsonElement> next = entries.iterator().next();

                    Module module = new Module(next.getKey());
                    container.add(module);

                    JsonElement value = next.getValue();
                    if (value.isJsonObject()) {
                        JsonObject valueObject = value.getAsJsonObject();

                        Set<Map.Entry<String, JsonElement>> attributes = valueObject.entrySet();
                        for (Map.Entry<String, JsonElement> attribute : attributes) {

                            String attributeKey = attribute.getKey();
                            String attributeValue = attribute.getValue().getAsString();

                            if ("id".equals(attributeKey)) {
                                module.setId(attributeValue);
                            }

                            module.addAttribute(attributeKey, attributeValue);
                        }
                    }

                } else {
                    // TODO : make these more descriptive!
                    throw new ContainerException(String.format("Unexpected json format"));
                }

            } else if (moduleElement.isJsonPrimitive()) {
                container.add(new Module(moduleElement.getAsString()));
            } else {
                // TODO : make these more descriptive!
                throw new ContainerException(String.format("Unexpected array element, wasn't an object or primative"));
            }
        }


        instantiate(container);

        return container;

    }


}
