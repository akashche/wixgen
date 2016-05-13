WiX Toolset Descriptors Generator
=================================

[WiX](http://wixtoolset.org/) is a toolset for creating Windows [MSI installers](https://en.wikipedia.org/wiki/Windows_Installer).

This project provides Java API (on top of [JAXB](https://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding))
that allows to generate WiX descriptors for installers from file system directories.

Generated installers have simple UI and supports the following features:

 - choose install path
 - set environment variables
 - set Windows registry keys
 - custom icon and logos

`wixgen.jar` utility allows to use that API from a command line.

Javadocs links:

  - [JAXB classes](http://akashche.github.io/wixgen/wixgen-jaxb/apidocs)
  - [`DirectoryGenerator` API](http://akashche.github.io/wixgen/wixgen-dir/apidocs)

Usage
-----

To generate an installer from the input directory prepare the following resources([examples](https://github.com/akashche/wixgen/tree/master/wixgen-dir/src/test/resources/com/redhat/akashche/wixgen/dir)):

 - greetings banner (BMP, 493x312)
 - top banner (BMP, 493x58)
 - application icon (ICO)
 - license file (RTF)
 - `wixgen` config file (JSON)

Generate WiX `.wxs` descriptor:

    java -jar wixgen.jar path/to/input/dir -c config.json -o myapp.wxs

Compile `.wxs` descriptor into `.wixobj` object file:

    candle myapp.wxs:

Build the installer from `.wixobj` object file:

    light -ext WixUIExtension myapp.wixobj

License information
-------------------

This project is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Changelog
---------

*2016-05-13*

 * initial public version