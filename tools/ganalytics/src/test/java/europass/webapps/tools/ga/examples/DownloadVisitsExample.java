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
package europass.webapps.tools.ga.examples;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.RealtimeData;
import com.google.api.services.analytics.model.Webproperties;

/**
 * This is a basic hello world sample for the Google Analytics API. It is
 * designed to run from the command line and will prompt a user to grant access
 * to their data. Once complete, the sample will traverse the Management API
 * hierarchy by going through the authorized user's first account, first web
 * property, and finally the first profile and retrieve the first profile id.
 * This ID is then used with the Core Reporting API to retrieve the top 25
 * organic search terms.
 *
 * @author api.nickm@gmail.com
 */
public class DownloadVisitsExample {

    /**
     * LOGGER
     */
    private static final Logger LOG = LoggerFactory.getLogger(DownloadVisitsExample.class);

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport httpTransport;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final HttpTransport TRANSPORT = new NetHttpTransport();

    /**
     * Be sure to specify the name of your application. If the application name
     * is {
     *
     * @table null} or blank, the application will log a warning. Suggested
     * format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "Europass Portal / EWA Editor TESTING";

    private static final String privateKeyFileName = "/europass.analytics.p12";

    private static final String serviceAccountID = "869004828392@developer.gserviceaccount.com";

    /**
     * Main demo. This first initializes an analytics service object. It then
     * uses the Google Analytics Management API to get the first profile ID for
     * the authorized user. It then uses the Core Reporting API to retrieve the
     * top 25 organic search terms. Finally the results are printed to the
     * screen. If an API error occurs, it is printed here.
     *
     * @param args command line args.
     */
    public static void main(String[] args) {

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            Analytics analytics = initializeAnalytics();

            LOG.info("Application Name: " + analytics.getApplicationName());

            String profileId = getFirstProfileId(analytics);

            if (profileId == null) {
                LOG.error("No profiles found.");
            } else {
                RealtimeData realData = executeDataQuery(analytics, profileId);
                printGaData(realData);
            }
        } catch (GoogleJsonResponseException e) {

            LOG.error("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private static Credential authorize() throws GeneralSecurityException, IOException {

        GoogleCredential credentials = null;

        List<String> serviceAccountScopes = new ArrayList<String>();
        serviceAccountScopes.add("https://www.googleapis.com/auth/analytics.readonly");

        LOG.info("Private Key from resource named " + privateKeyFileName + ":");

        PrivateKey pk = getPrivateKey(privateKeyFileName, "privatekey", "notasecret");

        if (pk == null) {
            throw new IOException("Private Key not found: " + privateKeyFileName);
        }

        LOG.info("\tPrivate Key Byte Size: " + pk.getEncoded().length);
        LOG.info("\tPrivate Key Algorithm/Format: " + pk.getAlgorithm() + "/" + pk.getFormat());

        credentials = new GoogleCredential.Builder()
                .setTransport(TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountID)
                .setServiceAccountScopes(serviceAccountScopes)
                .setServiceAccountPrivateKey(pk)
                .build();

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY, new InputStreamReader(
                        DownloadVisitsExample.class.getResourceAsStream("/client_secrets.json")));

        LOG.info("\tUser ID: " + clientSecrets.getWeb().getClientId());
        LOG.info("\tAccess Token URL: " + clientSecrets.getWeb().getTokenUri());

        return credentials;

    }

    /**
     * Constructs the private key
     *
     * @param keyFile
     * @param alias
     * @param password
     * @return
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     */
    private static PrivateKey getPrivateKey(String keyFile, String alias, String password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(DownloadVisitsExample.class.getResourceAsStream(keyFile), password.toCharArray());
        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password.toCharArray());

        return privateKey;
    }

    /**
     * Performs all necessary setup steps for running requests against the API.
     *
     * @return An initialized Analytics service object.
     *
     * @throws Exception if an issue occurs with OAuth2Native authorize.
     */
    private static Analytics initializeAnalytics() throws Exception {

        // Authorization.
        Credential credential = authorize();

        // Set up and return Google Analytics API client.
        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                APPLICATION_NAME).build();
    }

    /**
     * Returns the first profile id by traversing the Google Analytics
     * Management API. This makes 3 queries, first to the accounts collection,
     * then to the web properties collection, and finally to the profiles
     * collection. In each request the first ID of the first entity is retrieved
     * and used in the query for the next collection in the hierarchy.
     *
     * @param analytics the analytics service object used to access the API.
     * @return the profile ID of the user's first account, web property, and
     * profile.
     * @throws IOException if the API encounters an error.
     */
    private static String getFirstProfileId(Analytics analytics) throws IOException {
        String profileId = null;

        // Query accounts collection.
        Accounts accounts = analytics.management().accounts().list().execute();

        LOG.info("Accounts: " + accounts.getItems().size());

        if (accounts.getItems().isEmpty()) {
            LOG.error("No accounts found");
        } else {
            String firstAccountId = accounts.getItems().get(0).getId();

            // Query webproperties collection.
            Webproperties webproperties = analytics.management().webproperties().list(firstAccountId).execute();

            if (webproperties.getItems().isEmpty()) {
                LOG.error("No Webproperties found");
            } else {
                String firstWebpropertyId = webproperties.getItems().get(0).getId();

                // Query profiles collection.
                Profiles profiles = analytics.management().profiles().list(firstAccountId, firstWebpropertyId).execute();

                if (profiles.getItems().isEmpty()) {
                    LOG.error("No profiles found");
                } else {
                    profileId = profiles.getItems().get(0).getId();
                    LOG.info("Profile ID: " + profileId);
                }
            }
        }

        return profileId;
    }

    /**
     * Uses the the Core Reporting API to retrieve data.
     *
     * @param analytics the analytics service object used to access the API.
     * @param profileId the profile ID from which to retrieve data.
     * @return the response from the API.
     * @throws IOException tf an API error occured.
     */
    private static RealtimeData executeDataQuery(Analytics analytics, String profileId) throws IOException {

        return analytics.data().realtime().get("ga:" + profileId, // Table Id. ga: + profile id.
                "ga:eventValue,ga:totalEvents") // Metrics.
                .setDimensions("ga:eventCategory,ga:eventAction,ga:eventLabel")
                .execute();
    }

    /**
     * Prints the output from the Core Reporting API. The profile name is
     * printed along with each column name and all the data in the rows.
     *
     * @param results data returned from the Core Reporting API.
     */
    private static void printGaData(RealtimeData results) {
        LOG.info("printing results for profile: " + results.getProfileInfo().getProfileName());

        if (results.getRows() == null || results.getRows().isEmpty()) {
            LOG.info("No results Found.");
        } else {

            // Print column headers.
            for (com.google.api.services.analytics.model.RealtimeData.ColumnHeaders header : results.getColumnHeaders()) {
                System.out.printf("%30s", header.getName());
            }
            System.out.println();

            // Print actual data.
            for (List<String> row : results.getRows()) {
                for (String column : row) {
                    System.out.printf("%30s", column);
                }
                System.out.println();
            }

            System.out.println();
        }
    }
}
