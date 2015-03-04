package com.logginghub.container.samples;

import com.logginghub.container.loader.XmlImportResolverPreProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by chrisspikingdev on 04/03/2015.
 */
public class TestXmlImporting {

    @Test
    public void test_xml() throws Exception {
        XmlImportResolverPreProcessor xmlImportResolverPreProcessor = new XmlImportResolverPreProcessor();

        InputStream baseFile = ClassLoader.getSystemResourceAsStream("samples/basic_import_1a.xml");
        InputStream result = xmlImportResolverPreProcessor.preProcessFromInputStreamToInputStream(baseFile);
        final String resultAsString = IOUtils.toString(result, Charset.defaultCharset());
        System.out.print(resultAsString);
    }
}
