package main.islab1back.functions;

import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import main.islab1back.flats.controller.FlatWebSocketEndpoint;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Path("/functions")
public class FunctionsController {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myDb");
    private final EntityManager em = emf.createEntityManager();
    private final EntityTransaction transaction = em.getTransaction();
    FlatWebSocketEndpoint flatWebSocketEndpoint = new FlatWebSocketEndpoint();

    @PreDestroy
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Path("/view")
    @POST
    public Response viewDelete(@QueryParam("view") String viewValue) {
        try {
            transaction.begin();
            CallableStatement callableStatement = em.unwrap(Connection.class)
                    .prepareCall("{CALL delete_one_by_view(?)}");

            callableStatement.setString(1, viewValue);
            callableStatement.execute();
            flatWebSocketEndpoint.onMessage("");
            transaction.commit();
            return Response.ok().build();
        } catch (Exception e) {
            transaction.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("/prefix")
    @POST
    public Response findFlatPrefix(@QueryParam("prefix") String prefix) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            transaction.begin();
            CallableStatement callableStatement = em.unwrap(Connection.class)
                    .prepareCall("{CALL get_flat_by_name_prefix(?)}");

            callableStatement.setString(1, prefix);
            callableStatement.execute();
            ResultSet resultSet = callableStatement.getResultSet();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }
            transaction.commit();
            return Response.ok(result).build();
        } catch (Exception e) {
            transaction.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("/cheaper")
    @GET
    public Response cheaperFlat() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            transaction.begin();
            CallableStatement callableStatement = em.unwrap(Connection.class)
                    .prepareCall("{CALL get_cheapest_balcony_flat()}");
            callableStatement.execute();
            ResultSet resultSet = callableStatement.getResultSet();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }

            transaction.commit();
            if (!result.isEmpty()) {
                return Response.ok(result).build();
            } else {
                return Response.ok("Квартир с балконом нет").build();
            }
        } catch (Exception e) {
            transaction.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Path("/metro")
    @GET
    public Response metroFlat() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            transaction.begin();
            CallableStatement callableStatement = em.unwrap(Connection.class)
                    .prepareCall("{CALL get_sorted_flat_by_metro_time()}");
            callableStatement.execute();
            ResultSet resultSet = callableStatement.getResultSet();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                result.add(row);
            }

            transaction.commit();
            return Response.ok(result).build();
        } catch (Exception e) {
            transaction.rollback();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

}
