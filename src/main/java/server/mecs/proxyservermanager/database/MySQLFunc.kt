package server.mecs.proxyservermanager.database

import net.md_5.bungee.api.plugin.Plugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level

class MySQLFunc(host: String?, db: String?, user: String?, pass: String?, port: String?) {
    var HOST: String? = null
    var DB: String? = null
    var USER: String? = null
    var PASS: String? = null
    var PORT: String? = null
    var con: Connection? = null
    private val plugin: Plugin? = null
    fun open(): Connection? {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            con = DriverManager.getConnection("jdbc:mysql://$HOST:$PORT/$DB?useSSL=false", USER, PASS)
            return con
        } catch (var2: SQLException) {
            plugin!!.logger.log(Level.SEVERE, "Could not connect to MySQL server, error code: " + var2.errorCode)
        } catch (var3: ClassNotFoundException) {
            plugin!!.logger.log(Level.SEVERE, "JDBC driver was not found in this machine.")
        }
        return con
    }

    fun checkConnection(): Boolean {
        return con != null
    }

    fun close(c: Connection?) {
        var c = c
        c = null
    }

    init {
        HOST = host
        DB = db
        USER = user
        PASS = pass
        PORT = port
    }
}