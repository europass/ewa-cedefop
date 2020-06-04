/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package org.europass.webapps.tools.pdf2png;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.fileupload.InvalidFileNameException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

public class PDFtoPNGConvertor {

    private static CommandLine cmd = null;
    private static CommandLineParser parser = null;
    private static Options options;

    public static void main(String[] args) {

        String source = null;
        String prefix = null;
        String scale = null;
        String crop = null;

        try {
            parser = new PosixParser();
            Map<String, String> userArgsMap = getUserArgs(args);

            source = userArgsMap.get("i");
            if (!source.endsWith(".pdf")) {
                throw new InvalidFileNameException(source, "Source path file is not a pdf...");
            }

            prefix = userArgsMap.get("p");

            scale = userArgsMap.get("s");
            if (scale != null) {
                Float.parseFloat(scale);
            }

            crop = userArgsMap.get("c");
            if (crop != null) {
                Float.parseFloat(crop);
            }

            convertPDFtoPNG(source, (prefix == null ? "pfd2image" : prefix), scale, crop);

        } catch (InvalidFileNameException ex) {
            ex.printStackTrace();
            System.exit(-1);
        } catch (RuntimeException e) {
            if (e instanceof NumberFormatException) {
                System.out.println("Value is not a float ( " + e.getMessage() + " )");
            }
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static Map<String, String> getUserArgs(String[] args) {

        try {
            buildOptions();
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            argumentsFailureAndExit(pe.getMessage());
        }

        Map<String, String> argsMap = new HashMap<String, String>();

        if (cmd.getOptionValues("i") != null) {
            argsMap.put("i", cmd.getOptionValue("i"));
        }
        if (cmd.getOptionValues("p") != null) {
            argsMap.put("p", cmd.getOptionValue("p"));
        }
        if (cmd.getOptionValues("s") != null) {
            argsMap.put("s", cmd.getOptionValue("s"));
        }
        if (cmd.getOptionValues("c") != null) {
            argsMap.put("c", cmd.getOptionValue("c"));
        }

        return argsMap;
    }

    private static void buildOptions() {
        options = new Options();

        OptionBuilder.withLongOpt("input");
        OptionBuilder.hasArgs(1);
        OptionBuilder.isRequired(true);
        OptionBuilder.withDescription("The pdf source file path / name as input");
        options.addOption(OptionBuilder.create('i'));

        OptionBuilder.withLongOpt("prefix");
        OptionBuilder.hasArgs(1);
        OptionBuilder.isRequired(false);
        OptionBuilder.withDescription("(Optional) The image file(s) destination prefix name: <prefix>_pageX.png");
        options.addOption(OptionBuilder.create('p'));

        OptionBuilder.withLongOpt("scale");
        OptionBuilder.hasArgs(1);
        OptionBuilder.isRequired(false);
        OptionBuilder.withDescription("(Optional) The image scale ration as float, ex: 1.3");
        options.addOption(OptionBuilder.create('s'));

        OptionBuilder.withLongOpt("crop");
        OptionBuilder.hasArgs(1);
        OptionBuilder.isRequired(false);
        OptionBuilder.withDescription("(Optional) crop ratio as float that reduces the image height\n ex: 0.25 (for an image of height 400, the new height will be 100) ");
        options.addOption(OptionBuilder.create('c'));

    }

    private static void argumentsFailureAndExit(String errMsg) throws RuntimeException {

        System.err.println(errMsg);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar PDFtoPNGConvertor.jar", options);
        throw new RuntimeException();
    }

    private static void convertPDFtoPNG(String source, String prefix, String scaleRatio, String crop) throws org.icepdf.core.exceptions.PDFException, PDFSecurityException, IOException, URISyntaxException {

        File empty = new File("");
        String path = empty.getAbsolutePath() + "/";
        new File("_EXPORTED").mkdirs();
        empty.delete();

        URI pathURI = new File(source).toURI();

        Document document = new Document();
        document.setUrl(pathURI.toURL());

        // Paint each pages content to an image and
        // save page captures to file.
        float scale = scaleRatio != null ? Float.parseFloat(scaleRatio + "f") : 1.0f; // default 1.0f
        float rotation = 0f;

        // For permissions play with the
        // bits...http://res.icesoft.org/docs/icepdf/latest/core/org/icepdf/core/pobjects/security/Permissions.html
        // document.getSecurityManager().getPermissions()
        int pages = document.getNumberOfPages();

        for (int i = 0; i < pages; i++) {
            // write the image to file
            BufferedImage image = (BufferedImage) document.getPageImage(i,
                    GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX,
                    rotation, scale);

            BufferedImage renderedImage = image;

            if (crop != null) {
                Rectangle rect = new Rectangle(image.getWidth(), (int) (image.getHeight() * Float.parseFloat(crop)));
                renderedImage = image.getSubimage(0, 0, rect.width, rect.height);
            }

            File imageFile = new File(path + prefix + "_page" + i + ".png");
            ImageIO.write(renderedImage, "png", imageFile);

            image.flush();

        }
        // clean up resources
        document.dispose();
    }
}
