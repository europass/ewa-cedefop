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
package europass.ewa.database.guice;

import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.Scope;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletScopes;

public class HibernateModule extends AbstractModule {

    private final Properties properties;
    private final Scope unitOfWorkScope;

    /**
     * Integrate Hibernate into guice allowing Configuration, SessionFactory and
     * Session to be injected.
     *
     * @param properties SessionFactory properties.
     * @param resources Resource (.cfg.xml ) paths.
     */
    public HibernateModule(Properties properties) {
        this(properties, ServletScopes.REQUEST);
    }

    public HibernateModule(Properties properties, Scope unitOfWorkScope) {
        this.properties = properties;
        this.unitOfWorkScope = unitOfWorkScope;
    }

    @Override
    protected void configure() {

        bind(Properties.class).annotatedWith(Names.named("hibernate")).toInstance(properties);

        bind(SessionFactory.class).toProvider(SessionFactoryProvider.class).asEagerSingleton();

        bind(Session.class).toProvider(SessionProvider.class).in(unitOfWorkScope);

        bind(Transaction.class).toProvider(TransactionProvider.class).in(unitOfWorkScope);
    }

    static class SessionFactoryProvider implements Provider<SessionFactory> {

        private final Properties properties;

        private final Set<HibernateConfigurator> configurators;

        @Inject
        public SessionFactoryProvider(@Named("hibernate") Properties properties,
                Set<HibernateConfigurator> configurators) {
            super();
            this.properties = properties;
            this.configurators = configurators;
        }

        @Override
        public SessionFactory get() {
            //AnnotationConfiguration is deprecated in 4
            Configuration config = new Configuration();
            config.setProperties(properties);

            for (HibernateConfigurator configurator : configurators) {
                configurator.configure(config);
            }

            ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
                    .applySettings(config.getProperties())
                    .buildServiceRegistry();

            //SessionFactory requires a ServiceRegistry
            return config.buildSessionFactory(serviceRegistry);
        }

    }

    static class SessionProvider implements Provider<Session> {

        private final SessionFactory factory;

        @Inject
        public SessionProvider(SessionFactory factory) {
            this.factory = factory;
        }

        public Session get() {
            return factory.withOptions().openSession();
        }

    }

    static class TransactionProvider implements Provider<Transaction> {

        private final Session session;

        @Inject
        public TransactionProvider(Session session) {
            this.session = session;
        }

        public Transaction get() {
            return session.beginTransaction();
        }

    }

}
