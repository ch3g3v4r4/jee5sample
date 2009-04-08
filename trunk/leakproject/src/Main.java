import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.p6spy.engine.spy.P6DataSource;

public class Main {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            Connection conn = getP6Connection();
            Statement stmt = conn.createStatement();
            stmt.execute("Select 1");
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) System.out.println(rs.getString(1));
        }

    }

//    private static Connection getConnection() throws SQLException {
//        MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setUser("root");
//        dataSource.setPassword("");
//        dataSource.setServerName("localhost");
//        dataSource.setPort(3306);
//        dataSource.setDatabaseName("test");
//        return dataSource.getConnection();
//    }

    private static Connection getP6Connection() throws SQLException {
        // For using with datasource

        // If we use Spring, read http://templth.blogspot.com/2004/11/integrate-p6spy-with-spring.html

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("");
        mysqlDataSource.setServerName("localhost");
        mysqlDataSource.setPort(3306);
        mysqlDataSource.setDatabaseName("test");
        P6DataSource dataSource = new P6DataSource(mysqlDataSource);

        return dataSource.getConnection();
    }

}
