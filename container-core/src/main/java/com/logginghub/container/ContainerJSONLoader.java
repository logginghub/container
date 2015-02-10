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
        for (JsonElement module : modules) {

            if (module.isJsonObject()) {
                final JsonObject moduleAsJsonObject = module.getAsJsonObject();

                final Set<Map.Entry<String, JsonElement>> entries = moduleAsJsonObject.entrySet();
                if(entries.size() == 1) {
                    container.add(new Module(entries.iterator().next().getKey()));
                }else{
                    // TODO : make these more descriptive!
                    throw new ContainerException(String.format("Unexpected json format"));
                }

            } else if (module.isJsonPrimitive()) {
                container.add(new Module(module.getAsString()));
            } else {
                // TODO : make these more descriptive!
                throw new ContainerException(String.format("Unexpected array element, wasn't an object or primative"));
            }
        }


        instantiate(container);

        return container;

    }


}
