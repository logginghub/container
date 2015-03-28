package com.logginghub.container.samples;

import com.logginghub.container.loader.XmlImportResolverPreProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 * Created by chrisspikingdev on 04/03/2015.
 */
public class TestXmlImporting {

    @Test
    public void longFormatXmlImportProducesCorrectOutput() throws Exception {
        XmlImportResolverPreProcessor xmlImportResolverPreProcessor = new XmlImportResolverPreProcessor();

        InputStream baseFile = ClassLoader.getSystemResourceAsStream("samples/basic_import_1a.xml");
        InputStream result = xmlImportResolverPreProcessor.preProcessFromInputStreamToInputStream(baseFile);
        final String resultAsString = IOUtils.toString(result, Charset.defaultCharset());

        InputStream expectedResultIS = ClassLoader.getSystemResourceAsStream("samples/basic_import_1c.xml");
        final String expectedResultAsString = IOUtils.toString(expectedResultIS, Charset.defaultCharset());
        assertEquals(expectedResultAsString.replaceAll("\\s",""), resultAsString.replaceAll("\\s",""));
    }

    @Test
    public void shortFormatXmlImportProducesCorrectOutput() throws Exception {
        XmlImportResolverPreProcessor xmlImportResolverPreProcessor = new XmlImportResolverPreProcessor();

        InputStream baseFile = ClassLoader.getSystemResourceAsStream("samples/basic_import_2a.xml");
        InputStream result = xmlImportResolverPreProcessor.preProcessFromInputStreamToInputStream(baseFile);
        final String resultAsString = IOUtils.toString(result, Charset.defaultCharset());

        InputStream expectedResultIS = ClassLoader.getSystemResourceAsStream("samples/basic_import_1c.xml");
        final String expectedResultAsString = IOUtils.toString(expectedResultIS, Charset.defaultCharset());
        assertEquals(expectedResultAsString.replaceAll("\\s",""), resultAsString.replaceAll("\\s",""));
    }
}
