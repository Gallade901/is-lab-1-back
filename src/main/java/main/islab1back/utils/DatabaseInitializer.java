package main.islab1back.utils;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void initialize() {
        try {
            LOGGER.info("Initializing database functions...");

            createFunction("CREATE OR REPLACE FUNCTION delete_one_by_view(view_value TEXT) RETURNS VOID AS $$ " +
                    "BEGIN " +
                    "    DELETE FROM flat WHERE view = view_value AND id IN (SELECT id FROM apartments WHERE view = view_value LIMIT 1); " +
                    "END; $$ LANGUAGE plpgsql;");

            createFunction("CREATE OR REPLACE FUNCTION get_apartments_by_name_prefix(prefix TEXT) RETURNS SETOF flat AS $$ " +
                    "BEGIN RETURN QUERY SELECT * FROM flat WHERE name LIKE prefix || '%'; END; $$ LANGUAGE plpgsql;");

            createFunction("CREATE OR REPLACE FUNCTION get_cheapest_balcony_apartment() RETURNS SETOF flat AS $$ " +
                    "BEGIN RETURN QUERY SELECT * FROM flat WHERE has_balcony = TRUE ORDER BY price ASC LIMIT 1; END; $$ LANGUAGE plpgsql;");

            createFunction("CREATE OR REPLACE FUNCTION get_sorted_apartments_by_metro_time() RETURNS SETOF flat AS $$ " +
                    "BEGIN RETURN QUERY SELECT * FROM flat ORDER BY timeToMetroOnFoot ASC; END; $$ LANGUAGE plpgsql;");

            LOGGER.info("Database functions initialized successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database functions", e);
            throw new RuntimeException(e);
        }
    }

    private void createFunction(String sql) {
        entityManager.getTransaction().begin();
        try {
            entityManager.createNativeQuery(sql).executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }
    }
}