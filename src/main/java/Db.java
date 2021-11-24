import java.sql.*;
import java.util.Map;

public class Db {


    public static void main(String[] args) {
    }


    public static Connection connect(String db, String user, String pw) {
        try {
            Class.forName("com.mysql.cj.jdbc.driver");
        } catch (Exception ex) {
            // handle the error
        }

        Connection conn = null;
        try {
            String url = String.format("jdbc:mysql://localhost:8889/%s", db);
            conn = DriverManager.getConnection(url, user, pw);
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return conn;
    }


    public static void searchDb(Connection conn, String date, String id) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            String query = String.format("SELECT value FROM %s WHERE id = %s", date, id);
            rs = stmt.executeQuery(query);

            if (stmt.execute(query)) {
                rs = stmt.getResultSet();
            } else {
                System.out.println("Couldn't execute the query");
            }

            // Now do something with the ResultSet
            if (rs.next()) {
                String value = rs.getString(1);
                System.out.println("The exchange rate for " + id.replace("'", "")
                        + " at " + date.replace("`", "") + " is " + value);
                System.out.println("Data provided by DB");
            } else {
                // Gets here if table for the date exists but the id is wrong
                System.out.println("Please provide us with valid currency ID");
            }
        } catch (SQLException ex){
            // Gets here if there is no table for the date
            String dateForApi = date.replace("`", "");
            String idForApi = id.replace("'", "");
            String strForApi = dateForApi + " " + idForApi;

            try {
                Map<String, String> valueMap = Api.searchApi(strForApi);
                if (valueMap != null) {
                    addDb(conn, date, valueMap);
                    Currency currency = new Currency(id);
                    System.out.println("The exchange rate for " + id.replace("'", "")
                            + " at " + date.replace("`", "") + " is " + valueMap.get(idForApi));
                } else {
                    System.out.println("API couldn't execute your search request");
                    return;
                }
            } catch (java.io.IOException e) {
                System.out.println("Please, provide us with valid data");
            }
            System.out.println("This data was provided by the API and added to the DB");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }
                stmt = null;
            }
        }
    }


    public static String addDb(Connection conn, String date, Map<String, String> data) {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            String queryCreate = String.format("CREATE TABLE %s (id varchar(3), value varchar(100)); ", date);
            String queryInsert = String.format("INSERT INTO %s (id, value) VALUES ", date);
            Integer count = 0;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                count ++;
                queryInsert += "(" + "'" + entry.getKey() + "'" + ", " + "'" + entry.getValue() + "'" + ")";

                if (count != data.size()) {
                    queryInsert += ", ";
                } else {
                    queryInsert += ";";
                }
            }
            stmt.executeUpdate(queryCreate);
            stmt.executeUpdate(queryInsert);
        } catch (SQLException ex){
            // Gets here if there is no table for the date
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }
                stmt = null;
            }
        }
        return null;
    }
}
