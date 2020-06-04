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
package europass.ewa.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public abstract class ProtocolURLConnectionPoster implements URLConnectionPoster<HttpURLConnection> {

    protected String queryString;

    public ProtocolURLConnectionPoster(Map<String, String> params) throws IOException {
        this.queryString = this.formatParameters(params);
    }

    @Override
    public void connectionPost(HttpURLConnection connection) throws IOException {

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        // Added by dz on 13/4/2016 (otherwise utf8 chars are garbled in post-back requests)
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
        writer.write(this.queryString);
        writer.close();
        wr.flush();
        wr.close();
    }

    @Override
    public void writeResponse(HttpURLConnection connection) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

    }

    protected String formatParameters(Map<String, String> params) {

        Joiner paramValueJoin = Joiner.on("=");
        Joiner paramsJoin = Joiner.on("&");

        List<String> paramList = new ArrayList<String>();
        for (String param : params.keySet()) {
            paramList.add(paramValueJoin.join(param, params.get(param)));
        }

        return paramsJoin.join(paramList);

    }

}
