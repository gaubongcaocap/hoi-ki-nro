package jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import lombok.core.Main;

import utils.Logger;

public class DBConnecter {

    private static String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://%s:%s/%s?useUnicode=yes&characterEncoding=UTF-8";
    private static String DB_HOST = "160.191.244.129";
    private static String DB_PORT = "3306";
    public static String DB_DATA = "user";
    public static String DB_USER = "nroprime";
    private static String DB_PASSWORD = "150820";
    private static int MIN_CONN = 1;
    private static int MAX_CONN = 1;
    private static long MAX_LIFE_TIME = 120000L;
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    public static Connection getConnectionServer() throws SQLException {
        return ds.getConnection();
    }

    public static void close() {
        ds.close();
    }

    private static void loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("data/config/config.properties")) {
            properties.load(input);

            DRIVER = properties.getProperty("database.driver", DRIVER);
            DB_HOST = properties.getProperty("database.host", DB_HOST);
            DB_PORT = properties.getProperty("database.port", DB_PORT);
//            DB_SERVER = properties.getProperty("database.server", DB_SERVER);
            DB_DATA = properties.getProperty("database.name", DB_DATA);
            DB_USER = properties.getProperty("database.user", DB_USER);
            DB_PASSWORD = properties.getProperty("database.pass", DB_PASSWORD);
            MIN_CONN = Integer.parseInt(properties.getProperty("database.min", String.valueOf(MIN_CONN)));
            MAX_CONN = Integer.parseInt(properties.getProperty("database.max", String.valueOf(MAX_CONN)));
            MAX_LIFE_TIME = Long.parseLong(properties.getProperty("database.lifetime", String.valueOf(MAX_LIFE_TIME)));



        } catch (IOException | NumberFormatException e) {
            Logger.log("[4;31m", "Không thể load file properties!\n");
        }
    }

    public static NDVResultSet executeQuery(String query) throws Exception {
        try (Connection connection = DBConnecter.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            return new ResultSetImpl(preparedStatement.executeQuery());
        } catch (Exception e) {
            Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }
    }

    public static NDVResultSet executeQuery(String query, Object... params) throws Exception {
        try (Connection connection = DBConnecter.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return new ResultSetImpl(preparedStatement.executeQuery());
        } catch (Exception e) {
            Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }
    }

    public static int executeUpdate(String query) throws Exception {
        try (Connection connection = DBConnecter.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }
    }

    public static int executeUpdate(String query, Object... params) throws Exception {
        if (query.toLowerCase().startsWith("insert") && query.endsWith("()")) {
            StringBuilder placeholder = new StringBuilder();
            placeholder.append("(");
            for (int i = 0; i < params.length; i++) {
                placeholder.append("?");
                if (i < params.length - 1) {
                    placeholder.append(",");
                }
            }
            placeholder.append(")");
            query = query.replace("()", placeholder.toString());
        }

        try (Connection connection = DBConnecter.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }
    }

    static {
        loadProperties();
        config.setDriverClassName(DRIVER);
        config.setJdbcUrl(String.format(URL, DB_HOST, DB_PORT, DB_DATA));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMinimumIdle(MIN_CONN);
        config.setMaximumPoolSize(MAX_CONN);
        config.setMaxLifetime(MAX_LIFE_TIME);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "true");
        ds = new HikariDataSource(config);
    }
}
