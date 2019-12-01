import java.sql.*;

public class DBconnection
{
    private static Connection connection;

    private static String dbName = "sk3";
    private static String dbUser = "root";
    private static String dbPass = "testtest";

    private static StringBuilder insertQuery = new StringBuilder();
    private static int querySize = 10000;

    public static Connection getConnection()
    {
        if(connection == null)
        {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass +
                                "&useSSL=false"+
                                "&requireSSL=false"+
                                "&useLegacyDatetimeCode=false"+
                                "&amp"+
                                "&serverTimezone=UTC");
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE NOT NULL, " +
                        "`count` INT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "KEY(name(50)))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void countVoter(String name, String birthDay) throws SQLException
    {
        birthDay = birthDay.replace('.', '-');
        insertQuery.append(insertQuery.length() == 0 ? "" : " ,")
                .append("('")
                .append(name)
                .append("', '")
                .append(birthDay)
                .append("',1)");
        if (insertQuery.toString().getBytes().length >= querySize)
        {
            executeMuliInsert();
            insertQuery.delete(0,insertQuery.length());
        }
    }

    public static void executeMuliInsert() throws SQLException {
        String sql = "INSERT INTO voter_count(name,birthDate,`count`) " +
                "VALUES " + insertQuery.toString() + "ON DUPLICATE KEY UPDATE `count` = `count` + 1";
        DBconnection.getConnection().createStatement().execute(sql);
    }

    static void getQuerySize()
    {
        System.out.printf("Query size %,d bytes%n",insertQuery.toString().getBytes().length);
    }

    public static void printVoterCounts() throws SQLException
    {
        long start = System.currentTimeMillis();
        String sql = "SELECT name, birthDate, `count` FROM voter_count WHERE name = 'Фомагин Карл'";
        ResultSet rs = DBconnection.getConnection().createStatement().executeQuery(sql);
        while(rs.next())
        {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getInt("count"));
        }
        System.out.printf("%.3f sec.%n",(double)(System.currentTimeMillis() - start)/1000);
    }
}
