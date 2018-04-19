/*
 * Copyright 2016 akashche@redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.akashche.wixgen.cli;

import com.google.gson.Gson;
import com.redhat.akashche.wixgen.dir.DirectoryGenerator;
import com.redhat.akashche.wixgen.dir.WixConfig;
import com.redhat.akashche.wixgen.jaxb.Wix;
import org.apache.commons.cli.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;

import static java.lang.System.out;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * wixgen CLI tool entry point
 *
 * User: akashche
 */
public class Launcher {
    private static final String VERSION = "Wix Toolset Descriptors Generator 1.5.1";
    private static final String HELP_OPTION = "help";
    private static final String VERSION_OPTION = "version";
    private static final String CONFIG_OPTION = "config";
    private static final String XSL_OPTION = "xsl";
    private static final String OUTPUT_OPTION = "output";
    private static final Options OPTIONS = new Options()
            .addOption("h", HELP_OPTION, false, "show this page")
            .addOption("v", VERSION_OPTION, false, "show version")
            .addOption("c", CONFIG_OPTION, true, "configuration file")
            .addOption("x", XSL_OPTION, true, "xsl file")
            .addOption("o", OUTPUT_OPTION, true, "output file");

    public static void main(String[] args) throws Exception {
        try {
            CommandLine cline = new GnuParser().parse(OPTIONS, args);
            if (cline.hasOption(VERSION_OPTION)) {
                out.println(VERSION);
            } else if (cline.hasOption(HELP_OPTION)) {
                throw new ParseException("Printing help page:");
            } else if (1 == cline.getArgs().length &&
                    cline.hasOption(CONFIG_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION) &&
                    !cline.hasOption(XSL_OPTION)) {
                WixConfig conf = parseConf(cline.getOptionValue(CONFIG_OPTION));
                Wix wix = new DirectoryGenerator().createFromDir(new File(cline.getArgs()[0]), conf);
                writeXml(wix, cline.getOptionValue(OUTPUT_OPTION));
            } else if (1 == cline.getArgs().length &&
                    cline.hasOption(XSL_OPTION) &&
                    cline.hasOption(OUTPUT_OPTION)) {
                transformWithXsl(cline.getArgs()[0], cline.getOptionValue(XSL_OPTION), cline.getOptionValue(OUTPUT_OPTION));
            } else {
                throw new ParseException("Incorrect arguments received!");
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            out.println(e.getMessage());
            out.println(VERSION);
            formatter.printHelp("java -jar wixgen.jar input_dir -c config.json -o output.wxs", OPTIONS);
        }
    }

    private static void writeXml(Wix wix, String path) throws Exception {
        JAXBContext jaxb = JAXBContext.newInstance(Wix.class.getPackage().getName());
        Writer writer = null;
        try {
            OutputStream os = new FileOutputStream(new File(path));
            writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(wix, writer);
        } finally {
            closeQuietly(writer);
        }
    }

    private static WixConfig parseConf(String path) throws IOException {
        File file = new File(path);
        if (!(file.exists() && file.isFile())) throw new IOException("Invalid config file: [" + path + "]");
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            Reader re = new InputStreamReader(is, Charset.forName("UTF-8"));
            return new Gson().fromJson(re, WixConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid config file: [" + path + "]", e);
        } finally {
            closeQuietly(is);
        }
    }

    private static void transformWithXsl(String inputPath, String xslPath, String outputPath) throws Exception {
        File file = new File(outputPath);
        if (file.exists()) throw new IOException("Output file already exists: [" + outputPath + "]");
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xsl = new StreamSource(new File(xslPath));
        Transformer transformer = factory.newTransformer(xsl);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        Source text = new StreamSource(new File(inputPath));
        transformer.transform(text, new StreamResult(new File(outputPath)));
    }
}
