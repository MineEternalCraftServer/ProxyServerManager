package server.mecs.proxyservermanager.database;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bson.Document;
import server.mecs.proxyservermanager.ConfigFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MongoDBManager implements AutoCloseable {

    private Plugin plugin;
    public String HOST;
    public String USER;
    public String PASS;
    public int PORT;
    public String DATABASE;
    public String CUSTOM;
    private boolean connected = false;
    private MongoCollection<Document> coll;
    private MongoClient con = null;
    private MongoDBFunc MongoDB;

    /**
     * Created by Chiharu-Hagihara
     * Reference by takatronix:MySQLManager
     */

    ////////////////////////////////
    //      Constructor
    ////////////////////////////////
    public MongoDBManager(Plugin plugin, String coll) {
        this.plugin = plugin;

        loadConfig();

        this.connected = false;
        this.connected = Connect();
        this.coll = con.getDatabase(DATABASE).getCollection(coll);

        if (!this.connected) {
            this.plugin.getLogger().info("Unable to establish a MongoDB connection.");
        }
    }

    /////////////////////////////////
    //       Load YAML
    /////////////////////////////////
    public void loadConfig() {
        plugin.getLogger().info("MongoDB Config loading");

        Configuration config = new ConfigFile(plugin).getConfig();
        HOST = config.getString("mongo.host");
        USER = config.getString("mongo.user");
        PASS = config.getString("mongo.pass");
        PORT = config.getInt("mongo.port");
        CUSTOM = config.getString("mongo.uri");
        DATABASE = config.getString("mongo.db");

        plugin.getLogger().info("Config loaded");

    }

    ////////////////////////////////
    //       Connect
    ////////////////////////////////
    public Boolean Connect() {
        this.MongoDB = new MongoDBFunc(HOST, USER, PASS, PORT, DATABASE, CUSTOM);
        this.con = this.MongoDB.open();
        if (this.con == null) {
            plugin.getLogger().info("failed to open MongoDB");
            return false;
        }

        try {
            this.connected = true;
            this.plugin.getLogger().info("Connected to the database.");
        } catch (Exception var6) {
            this.connected = false;
            this.plugin.getLogger().info("Could not connect to the database.");
        }

        this.MongoDB.close(this.con);
        return this.connected;
    }

    ////////////////////////////////
    //       InsertOne Query
    ////////////////////////////////
    public void queryInsertOne(String doc) {
        coll.insertOne(Document.parse(doc));
    }

    ////////////////////////////////
    //       UpdateOne Query
    ////////////////////////////////
    public void queryUpdateOne(String filter, String update) {
        coll.updateOne(Document.parse(filter), Document.parse(update));
    }

    ////////////////////////////////
    //       DeleteOne Query
    ////////////////////////////////
    public void queryDelete(String filter) {
        coll.deleteOne(Document.parse(filter));
    }

    ////////////////////////////////
    //       Find Query
    ////////////////////////////////
    public List<Document> queryFind(String key, String value) {
        BasicDBObject query = new BasicDBObject(key, value);
        return coll.find(query).into(new ArrayList<>());
    }

    ////////////////////////////////
    //       Count Query
    ////////////////////////////////
    public long queryCount(String doc) {
        return coll.countDocuments(Document.parse(doc));
    }

    ////////////////////////////////
    //       Connection Close
    ////////////////////////////////
    @Override
    public void close() {

        try {
            this.con.close();
            this.MongoDB.close(this.con);

        } catch (Exception var4) {
        }

    }

    ////////////////////////////////
    //       Setup BlockingQueue
    ////////////////////////////////
    static LinkedBlockingQueue<String> loginLogQueue = new LinkedBlockingQueue<>();
    static LinkedBlockingQueue<String> logoutLogQueue = new LinkedBlockingQueue<>();
    static LinkedBlockingQueue<String> chatLogQueue = new LinkedBlockingQueue<>();

    public static void setupBlockingLoginQueue(Plugin plugin, String coll) {
        new Thread(() -> {
            MongoDBManager mongo = new MongoDBManager(plugin, coll);
            try {
                while (true) {
                    String take = loginLogQueue.take();
                    mongo.queryInsertOne(take);
                }
            } catch (Exception e) {
            }
        }).start();
    }

    public static void setupBlockingLogoutQueue(Plugin plugin, String coll) {
        new Thread(() -> {
            MongoDBManager mongo = new MongoDBManager(plugin, coll);
            try {
                while (true) {
                    String take = logoutLogQueue.take();
                    mongo.queryInsertOne(take);
                }
            } catch (Exception e) {
            }
        }).start();
    }

    public static void setupBlockingChatQueue(Plugin plugin, String coll) {
        new Thread(() -> {
            MongoDBManager mongo = new MongoDBManager(plugin, coll);
            try {
                while (true) {
                    String take = chatLogQueue.take();
                    mongo.queryInsertOne(take);
                }
            } catch (Exception e) {
            }
        }).start();
    }

    public static void executeLoginQueue(String query) {
        loginLogQueue.add(query);
    }
    public static void executeLogoutQueue(String query) {
        logoutLogQueue.add(query);
    }
    public static void executeChatQueue(String query) {
        chatLogQueue.add(query);
    }
}
