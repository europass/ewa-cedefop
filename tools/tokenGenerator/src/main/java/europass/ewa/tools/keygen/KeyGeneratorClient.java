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
package europass.ewa.tools.keygen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.validator.routines.UrlValidator;

import europass.ewa.tools.keygen.generator.TokenGenerator;
import europass.ewa.tools.keygen.generator.URLKeyGenerator;

/**
 * TokenGenerator: command line utility to generate keys for partner URL keys
 *
 * @input: URL's, list of values
 * @output: random token in console output, or file
 */
public class KeyGeneratorClient {

    private static CommandLine cmd = null;
    private static CommandLineParser parser = null;
    private static Options options;

    public static void main(String[] args) {
        Map<String, List<String>> userArgsMap = null;

        try {
            parser = new PosixParser();
            userArgsMap = getUserArgs(args);
        } catch (RuntimeException e) {
            System.exit(-1);
        }

        UrlValidator urlValidator = new UrlValidator();
        List<String> seeds = new ArrayList<>();
        TokenGenerator keygen = new URLKeyGenerator();

        for (List<String> urls : userArgsMap.values()) {
            for (String url : urls) {
                if (!urlValidator.isValid(url)) {
                    argumentsFailureAndExit("URL :" + url + " is not valid.");
                }
            }
            try {
                seeds = keygen.generateToken(urls); //call seed generator
            } catch (Exception e) {
                argumentsFailureAndExit("Application error. Please try again.");
            }
        }

        //print seeds to console, TODO write in properties file
        System.out.println("URL ::: TOKEN");
        int i = 0;
        for (String value : userArgsMap.get("u")) {
            System.out.println(value + " : " + seeds.get(i++));
        }
    }

    private static Map<String, List<String>> getUserArgs(String[] args) {

        buildOptions(args.length);
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException pe) {
            argumentsFailureAndExit(pe.getMessage());
        }

        Map<String, List<String>> userArgsMap = new HashMap<String, List<String>>();

        List<String> urls = new ArrayList<String>();
        if (cmd.getOptionValues("u") != null) {
            for (int i = 0; i < cmd.getOptionValues("u").length; i++) {
                urls.add(cmd.getOptionValues("u")[i]);
            }
        }

        userArgsMap.put("u", urls);

        return userArgsMap;

    }

    private static void buildOptions(int argCount) {
        options = new Options();

        OptionBuilder.withLongOpt("url");
        OptionBuilder.hasArgs(argCount);
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.isRequired(true);
        OptionBuilder.withDescription("The valid URL(s) provided. Example: http://wiki.com https://wiki.net");
        options.addOption(OptionBuilder.create('u'));
    }

    private static void argumentsFailureAndExit(String errMsg) throws RuntimeException {

        System.err.println(errMsg);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar TokenGenerator.jar", options);
        throw new RuntimeException();
    }

}
