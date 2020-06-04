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
package europass.ewa.extraction;

import com.google.inject.name.Named;
import europass.ewa.extraction.modules.AnpalExtractorModule;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author at
 */
public class AnpalExtractor {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AnpalExtractor.class);

    private final String anpalServiceToken;
    private final String anpalmagesRestUrl;
    private final String anpalTextsRestUrl;
    private final String jsResourcesPath;
    private final String imagesPath;
    private final String apiResourcesPath;
    private final String editorsBaseDir = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().getParent().toString();

    @Inject
    public AnpalExtractor(
            @Named(AnpalExtractorModule.ANPAL_SERVICE_TOKEN) String serviceToken,
            @Named(AnpalExtractorModule.IMAGES_REST_URL) String imagesRestUrl,
            @Named(AnpalExtractorModule.TEXTS_REST_URL) String textsRestUrl,
            @Named(AnpalExtractorModule.EDITORS_RESOURCES_IMAGES_PATH) String imagesPath,
            @Named(AnpalExtractorModule.EDITORS_RESOURCES_PATH) String jsResourcesPath,
            @Named(AnpalExtractorModule.API_RESOURCES_PATH) String apiResourcesPath) {

        this.anpalServiceToken = serviceToken;
        this.anpalmagesRestUrl = imagesRestUrl;
        this.anpalTextsRestUrl = textsRestUrl;
        this.jsResourcesPath = jsResourcesPath;
        this.imagesPath = imagesPath;
        this.apiResourcesPath = apiResourcesPath;
    }

    public void getImages() {
        int status = 0;
        try {

            HttpClient client = HttpClientBuilder.create().setSSLSocketFactory(createSSLConnectionFactory()).build();

            HttpPost post = new HttpPost(anpalmagesRestUrl);
            post.setEntity(new StringEntity("{\"locale\":\"it\"}"));
            post.setHeader("X-IBM-Client-Id", anpalServiceToken);
            post.addHeader("Content-Type", "application/json");

            final HttpResponse resp = client.execute(post);

            status = resp.getStatusLine().getStatusCode();

            if (status == 200) {
                String responseString = EntityUtils.toString(resp.getEntity());
                JSONObject responseJson = new JSONObject(responseString);

                String imgBase64 = responseJson.getJSONObject("data").getString("banner");

                byte[] decoded = Base64.decodeBase64(imgBase64);

                OutputStream out = new FileOutputStream(editorsBaseDir + File.separator + imagesPath + File.separator + "anpal.jpg");
                out.write(decoded);
                out.close();
            } else {
                throw new Exception("Error when posting to ANPAL images endpoint.");
            }

        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            LOG.error("Failed to fetch ANPAL banner image ", ex);
        } catch (Exception ex) {
            LOG.error("Error when posting to ANPAL images endpoint. Status code: " + status, ex);
        }

    }

    public JSONObject getTexts() {
        int status = 0;
        JSONObject textsJson = null;
        try {
            final HttpClient client = HttpClientBuilder.create().setSSLSocketFactory(createSSLConnectionFactory()).build();

            HttpPost post = new HttpPost(anpalTextsRestUrl);
            post.setEntity(new StringEntity("{\"locale\":\"it\"}"));
            post.setHeader("X-IBM-Client-Id", anpalServiceToken);
            post.addHeader("Content-Type", "application/json");
            LOG.info("Extracting texts from infocamere service: " + anpalTextsRestUrl);
            LOG.info("Service token: " + anpalServiceToken);

            final HttpResponse resp = client.execute(post);

            status = resp.getStatusLine().getStatusCode();
            LOG.info("Response status: " + status);
            if (status == 200) {
                JSONObject responseJson = new JSONObject(EntityUtils.toString(resp.getEntity(), "UTF-8"));
                JSONArray data = responseJson.getJSONArray("data");

                textsJson = new JSONObject();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject arrayObject = data.getJSONObject(i);
                    String arrayObjectName = arrayObject.getString("name");
                    textsJson.put("export.wizard.anpal." + arrayObjectName, arrayObject.getString("text"));
                }

                textsJson.put("export.wizard.anpal.version", !responseJson.isNull("version") ? responseJson.getString("version") : "");

                //resource is needed in the editors module (js) and in api (json)
                final FileOutputStream outJson = new FileOutputStream(new File(editorsBaseDir + File.separator + apiResourcesPath + File.separator + "GuiLabelExtra_it.json"));
                outJson.write((textsJson.toString()).getBytes("UTF-8"));
                outJson.close();

                final FileOutputStream outJs = new FileOutputStream(new File(editorsBaseDir + File.separator + jsResourcesPath + File.separator + "it" + File.separator + "GuiLabelExtra.js"));
                outJs.write(("define(" + textsJson.toString() + ")").getBytes("UTF-8"));
                outJs.close();

            } else {
                throw new Exception("Error when posting to ANPAL texts endpoint.");
            }

        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            LOG.error("Failed to fetch ANPAL texts ", ex);
        } catch (Exception ex) {
            LOG.error("Error when posting to ANPAL texts endpoint. Status code: " + status, ex);
        }
        return textsJson;
    }

    private SSLConnectionSocketFactory createSSLConnectionFactory() throws KeyManagementException, NoSuchAlgorithmException {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }}, new SecureRandom());

        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        return new SSLConnectionSocketFactory(sslContext, allowAllHosts);
    }
}
