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
package europass.ewa.oo.client;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import europass.ewa.oo.client.module.OfficeClientModule;

public class TestingClient {

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(
                new AbstractModule() {

            @Override
            protected void configure() {
                bindConstant().annotatedWith(
                        Names.named(OfficeClientModule.OPEN_OFFICE_CLIENT_SERVERS_PARAM)).
                        to("http://localhost:8080/ewaoffice/office");
            }

        },
                new OfficeClientModule());

        List<String> list = new ArrayList<String>();
        try {
            list.add("http://localhost:8080/ewaoffice/office");

            OfficeClient noc = injector.getInstance(OfficeClientImplementation.class);

            System.out.println("Generate Threads");
            ArrayList<TestClientThread> threads = new ArrayList<TestClientThread>();
            for (int i = 0; i < 20; i++) {

                TestClientThread th = new TestClientThread(noc, i);
                threads.add(th);
            }

            System.out.println("Starting Threads");
            for (int i = 0; i < 20; i++) {
                System.out.println("Starting " + i);
                TestClientThread th = threads.get(i);
                th.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
