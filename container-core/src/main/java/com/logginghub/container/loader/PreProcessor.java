package com.logginghub.container.loader;

import java.io.InputStream;

/**
 * @author cspiking
 */
public interface PreProcessor {

    InputStream preProcessFromInputStreamToInputStream(InputStream inputStream);

}
