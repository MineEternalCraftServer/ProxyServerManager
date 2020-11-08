package server.mecs.proxyservermanager.database;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import server.mecs.proxyservermanager.ConfigFile;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class MySQLManager {
    Boolean debugMode = false;
    String HOST = null;
    String USER = null;
    String PASS = null;
    String PORT = null;
    String DB = null;
    Boolean connected = false;
    Statement st = null;
    Connection connection = null;
    String conName = null;
    MySQLFunc MySQL = null;

    private Plugin plugin;

    public MySQLManager(Plugin plugin, String conName) {
        this.plugin = plugin;
        this.conName = conName;
        connected = false;
        loadConfig();
    }

    void loadConfig() {
        Configuration config = new ConfigFile(plugin).getConfig();
        HOST = config.getString("mysql.host");
        USER = config.getString("mysql.user");
        PASS = config.getString("mysql.pass");
        PORT = "3306";
        DB = config.getString("mysql.db");
        plugin.getLogger().info("Config loaded");
    }

    void commit() {
        try {
            this.connection.commit();
        } catch (SQLException e) {

        }
    }

    public Boolean Connect(String host, String db, String user, String pass, String port) {
        this.HOST = host;
        this.DB = db;
        this.USER = user;
        this.PASS = pass;
        this.MySQL = new MySQLFunc(host, db, user, pass, port);
        this.connection = this.MySQL.open();
        if (this.connection == null) {
            this.plugin.getLogger().info("failed to open MYSQL");
            return false;
        }

        try {
            this.st = this.connection.createStatement();
            this.connected = true;
            this.plugin.getLogger().info("[" + this.conName + "] Connected to the database.");
        } catch (SQLException var6) {
            this.connected = false;
            this.plugin.getLogger().info("[" + this.conName + "] Could not connect to the database.");
        }

        this.MySQL.close(this.connection);
        return Boolean.valueOf(this.connected);
    }

    public int countRows(String table) {
        int count = 0;
        ResultSet set = this.query(String.format("SELECT * FROM %s", new Object[]{table}));

        try {
            while (set.next()) {
                ++count;
            }
        } catch (SQLException var5) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not select all rows from table: " + table + ", error: " + var5.getErrorCode());
        }

        return count;
    }

    public int count(String table) {
        int count = 0;
        ResultSet set = this.query(String.format("SELECT count(*) from %s", table));

        try {
            count = set.getInt("count(*)");

        } catch (SQLException var5) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not select all rows from table: " + table + ", error: " + var5.getErrorCode());
            return -1;
        }

        return count;
    }

    public boolean execute(String query) {
        this.MySQL = new MySQLFunc(this.HOST, this.DB, this.USER, this.PASS, this.PORT);
        this.connection = this.MySQL.open();
        if (this.connection == null) {
            this.plugin.getLogger().info("failed to open MYSQL");
            return false;
        }
        boolean ret = true;
        if (debugMode) {
            plugin.getLogger().info("query:" + query);
        }

        try {
            this.st = this.connection.createStatement();
            this.st.execute(query);
        } catch (SQLException var3) {
            this.plugin.getLogger().info("[" + this.conName + "] Error executing statement: " + var3.getErrorCode() + ":" + var3.getLocalizedMessage());
            this.plugin.getLogger().info(query);
            ret = false;

        }

        this.close();
        return ret;
    }

    public ResultSet query(String query) {
        this.MySQL = new MySQLFunc(this.HOST, this.DB, this.USER, this.PASS, this.PORT);
        this.connection = this.MySQL.open();
        ResultSet rs = null;
        if (this.connection == null) {
            this.plugin.getLogger().info("failed to open MYSQL");
            return rs;
        }

        if (debugMode) {
            plugin.getLogger().info("[DEBUG] query:" + query);
        }

        try {
            this.st = this.connection.createStatement();
            rs = this.st.executeQuery(query);
        } catch (SQLException var4) {
            this.plugin.getLogger().info("[" + this.conName + "] Error executing query: " + var4.getErrorCode());
            this.plugin.getLogger().info(query);
        }

//        this.close();

        return rs;
    }


    public void close() {

        try {
            this.st.close();
            this.connection.close();
            this.MySQL.close(this.connection);

            plugin.getLogger().info("closed " + this.conName);
        } catch (SQLException var4) {
        }

    }
}
