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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpURLConnectionPosterImpl extends ProtocolURLConnectionPoster {

    HttpURLConnection connection;

    public HttpURLConnectionPosterImpl(URL url, Map<String, String> params) throws IOException {
        super(params);
        this.connection = connectionOpen(url);
    }

    @Override
    public void connectionPost(HttpURLConnection connection) throws IOException {
        super.connectionPost(connection);
    }

    @Override
    public int getCode() throws IOException {
        return this.connection.getResponseCode();
    }

    private HttpURLConnection connectionOpen(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

}
