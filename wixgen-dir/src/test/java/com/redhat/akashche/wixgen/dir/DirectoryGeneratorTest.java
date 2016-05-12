package com.redhat.akashche.wixgen.dir;

import com.redhat.akashche.wixgen.jaxb.Wix;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.Charset;

import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * User: alexkasko
 * Date: 5/12/16
 */
public class DirectoryGeneratorTest {

    @Test
    public void test() throws Exception {
        Wix wix = new DirectoryGenerator().createFromDir(new File("src"));
        JAXBContext jaxb = JAXBContext.newInstance(Wix.class.getPackage().getName());
        Writer writer = null;
        try {
            OutputStream os = new FileOutputStream("target/test.wxs");
            writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(wix, writer);
        } finally {
            closeQuietly(writer);
        }
    }
}
