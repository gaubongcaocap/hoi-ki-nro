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
import utils.Logger;

public class NDVDB {

    private static String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://%s:%s/%s?useUnicode=yes&characterEncoding=UTF-8";
    private static String DB_HOST = "localhost";
    private static String DB_PORT = "3306";
//    private static String DB_NAME = "linhthuydanhbac";
    private static String DB_SERVER = "ngocrong_user";
    public static String DB_DATA = "ngocrong_data";
    public static String DB_USER = "root";
    private static String DB_PASSWORD = "";
    private static int MIN_CONN = 1;
    private static int MAX_CONN = 1;
    private static long MAX_LIFE_TIME = 120000L;
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    private static final HikariConfig config2 = new HikariConfig();
    private static final HikariDataSource ds2;

    public static Connection getConnectionServer() throws SQLException {
        return ds.getConnection();
    }

    public static Connection getConnectionDATA() throws SQLException {
        return ds2.getConnection();
    }

    public static void close() {
        ds.close();
        ds2.close();
    }

    private static void loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("data/config/config.properties")) {
            properties.load(input);

            DRIVER = properties.getProperty("database.driver", DRIVER);
            DB_HOST = properties.getProperty("database.host", DB_HOST);
            DB_PORT = properties.getProperty("database.port", DB_PORT);
            DB_SERVER = properties.getProperty("database.server", DB_SERVER);
            DB_DATA = properties.getProperty("database.data", DB_DATA);
            DB_USER = properties.getProperty("database.user", DB_USER);
            DB_PASSWORD = properties.getProperty("database.pass", DB_PASSWORD);
            MIN_CONN = Integer.parseInt(properties.getProperty("database.min", String.valueOf(MIN_CONN)));
            MAX_CONN = Integer.parseInt(properties.getProperty("database.max", String.valueOf(MAX_CONN)));
            MAX_LIFE_TIME = Long.parseLong(properties.getProperty("database.lifetime", String.valueOf(MAX_LIFE_TIME)));

            Logger.warning("  _         _____     _    _    _____    _____         ____    _____     _   __   _    _   \n");
            Logger.warning(" | |       |  _  |   | |  | |  |_   _|  /  ___|       / ___|  |  _  |   | | / /  | |  | |  \n");
            Logger.warning(" | |       | | | |   | |  | |    | |    \\ `--.       | |  _   | | | |   | |/ /   | |  | |  \n");
            Logger.warning(" | |       | | | |   | |  | |    | |     `--. \\      | | | |  | | | |   |    \\   | |  | |  \n");
            Logger.warning(" | |____   | |_| |   | |_/ /    _| |_   /\\__/ /      | |_| |  | |_| |   | |\\  \\  | |_/ /   \n");
            Logger.warning(" |_____/   \\_____/   \\____/    |_____|  \\____/        \\____|  \\_____/   \\_| \\_/  \\____/    \n");
            Logger.log("[0;32m", "Successfully loaded file properties!\n");

        } catch (IOException | NumberFormatException e) {
            Logger.log("[4;31m", "Không thể load file properties!\n");
        }
    }

    public static NDVResultSet executeQuery(String query) throws Exception {
        try (Connection connection = NDVDB.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            return new ResultSetImpl(preparedStatement.executeQuery());
        } catch (Exception e) {
            try (Connection connection = NDVDB.getConnectionDATA(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                return new ResultSetImpl(preparedStatement.executeQuery());
            } catch (Exception ex) {
                Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
                throw e;
            }
        }
    }

    public static NDVResultSet executeQuery(String query, Object... params) throws Exception {
        try (Connection connection = NDVDB.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return new ResultSetImpl(preparedStatement.executeQuery());
        } catch (Exception e) {
            try (Connection connection = NDVDB.getConnectionDATA(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
                return new ResultSetImpl(preparedStatement.executeQuery());
            } catch (Exception ex) {
                Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
                throw e;
            }
        }
    }

    public static int executeUpdate(String query) throws Exception {
        try (Connection connection = NDVDB.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            try (Connection connection = NDVDB.getConnectionDATA(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                return preparedStatement.executeUpdate();
            } catch (Exception ex) {
                Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
                throw e;
            }
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

        try (Connection connection = NDVDB.getConnectionServer(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            try (Connection connection = NDVDB.getConnectionDATA(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + 1, params[i]);
                }
                return preparedStatement.executeUpdate();
            } catch (Exception ex) {
                Logger.log("[4;31m", "Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
                throw e;
            }
        }
    }

    static {
        loadProperties();
        config.setDriverClassName(DRIVER);
        config.setJdbcUrl(String.format(URL, DB_HOST, DB_PORT, DB_SERVER));
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

        config2.setDriverClassName(DRIVER);
        config2.setJdbcUrl(String.format(URL, DB_HOST, DB_PORT, DB_DATA));
        config2.setUsername(DB_USER);
        config2.setPassword(DB_PASSWORD);
        config2.setMinimumIdle(MIN_CONN);
        config2.setMaximumPoolSize(MAX_CONN);
        config2.setMaxLifetime(MAX_LIFE_TIME);
        config2.addDataSourceProperty("cachePrepStmts", "true");
        config2.addDataSourceProperty("prepStmtCacheSize", "250");
        config2.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config2.addDataSourceProperty("useServerPrepStmts", "true");
        config2.addDataSourceProperty("useLocalSessionState", "true");
        config2.addDataSourceProperty("rewriteBatchedStatements", "true");
        config2.addDataSourceProperty("cacheResultSetMetadata", "true");
        config2.addDataSourceProperty("cacheServerConfiguration", "true");
        config2.addDataSourceProperty("elideSetAutoCommits", "true");
        config2.addDataSourceProperty("maintainTimeStats", "true");
        ds2 = new HikariDataSource(config2);
    }
}
