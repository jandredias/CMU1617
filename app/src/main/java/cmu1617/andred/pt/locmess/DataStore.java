package cmu1617.andred.pt.locmess;

/**
 * Created by andre on 07/01/17.
 */

public interface DataStore {

    String SQL_LOCATION = "wifi_location";
    String SQL_WIFI_LOCATION_SSID = "wifi_location_ssid";
    String SQL_GPS_LOCATION = "gps_location";
    String SQL_LOGIN = "sql_login";
    String SQL_KEYWORDS = "sql_keywords";
    String SQL_USER_KEYWORDS = "sql_user_keywords";
    String SQL_MESSAGES = "sql_messages";
    String SQL_READ_MESSAGES = "sql_read_messages";
    String SQL_WIFI_MESSAGES="sql_wifi_messages";


    String[] SQL_LOCATION_COLUMNS = {
            "location_id",
            "name"
    };
    String[] SQL_WIFI_LOCATION_SSID_COLUMNS = {
            "location_id",
            "ssid",
            "enabled"
    };
    String[] SQL_GPS_LOCATION_COLUMNS = {
            "location_id",
            "latitude",
            "longitude",
            "radius",
            "enabled"
    };
    String[] SQL_LOGIN_COLUMNS = {
            "username",
            "access_token",
            "refresh_token",
            "valid"
    };
    String[] SQL_KEYWORDS_COLUMNS = {
            "keyword_id",
            "keyword_name"
    };
    String[] SQL_USER_KEYWORDS_COLUMNS = {
            "keyword_id",
            "keyword_value"
    };
    String[] SQL_MESSAGES_COLUMNS = {
            "message_id",
            "content",
            "author_id",
            "location_id",
            "time_start",
            "time_end",
            "post_timestamp",
            "enabled"
    };
    String[] SQL_READ_MESSAGES_COLUMNS = {
            "message_id",
            "content",
            "author_id",
            "reader_id",
            "location_id",
            "post_timestamp"
    };
    String[] SQL_WIFI_MESSAGES_COLUMNS = {
            "message_id",
            "content",
            "author_id",
            "location_id",
            "time_start",
            "time_end",
            "jumped"
    };



    String SQL_CREATE_LOCATION =
            "CREATE TABLE " + SQL_LOCATION + " (" +
            "location_id INT NOT NULL," +
            "name VARCHAR(255) )";
    String SQL_CREATE_WIFI_LOCATION_SSID =
            "CREATE TABLE " + SQL_WIFI_LOCATION_SSID + " (" +
                    "location_id INT NOT NULL," +
                    "ssid VARCHAR(255)," +
                    "enabled BOOLEAN, " +
                    "unique(location_id, ssid)"+
                    ")";
    String SQL_CREATE_GPS_LOCATION =
            "CREATE TABLE " + SQL_GPS_LOCATION + " (" +
                    "location_id INT UNIQUE NOT NULL," +
                    "latitude DOUBLE PRECISION,"+
                    "longitude DOUBLE PRECISION,"+
                    "radius INT," +
                    "enabled BOOLEAN" +
                    ")";
    String SQL_CREATE_LOGIN =
            "CREATE TABLE " + SQL_LOGIN + " (" +
                    "username VARCHAR(255) NOT NULL PRIMARY KEY," +
                    "access_token VARCHAR(255) NOT NULL,"+
                    "refresh_token VARCHAR(255) NOT NULL,"+
                    "valid BOOLEAN NOT NULL"+
                    ")";
    String SQL_CREATE_KEYWORDS =
            "CREATE TABLE " + SQL_KEYWORDS + " (" +
                    "keyword_id INT NOT NULL," +
                    "keyword_name VARCHAR(255)" +
                    ")";
    String SQL_CREATE_USER_KEYWORDS =
            "CREATE TABLE " + SQL_USER_KEYWORDS+ " (" +
                    "keyword_id INT NOT NULL PRIMARY KEY," +
                    "keyword_value VARCHAR(255)" +
                    ")";
    String SQL_CREATE_MESSAGES =
            "CREATE TABLE " + SQL_MESSAGES+ " (" +
                    "message_id VARCHAR(255) NOT NULL PRIMARY KEY,"+
                    "content VARCHAR (20000),"+
                    "author_id VARCHAR(255)," +
                    "location_id INT ,"+
                    "time_start DATETIME ,"+
                    "time_end DATETIME,"+
                    "post_timestamp DATETIME,"+
                    "enabled boolean"+
                    ")";

    String SQL_CREATE_READ_MESSAGES =
            "CREATE TABLE " + SQL_READ_MESSAGES+ " (" +
                    "message_id INT NOT NULL PRIMARY KEY,"+
                    "content VARCHAR (20000),"+
                    "author_id VARCHAR(255)," +
                    "reader_id VARCHAR(255)," +
                    "location_id INT, "+
                    "post_timestamp DATETIME"+
                    ")";
    String SQL_CREATE_WIFI_MESSAGES =
            "CREATE TABLE " + SQL_WIFI_MESSAGES+ " (" +
                    "message_id VARCHAR(255) NOT NULL PRIMARY KEY,"+
                    "content VARCHAR (20000),"+
                    "author_id VARCHAR(255)," +
                    "location_id INT ,"+
                    "time_start DATETIME ,"+
                    "time_end DATETIME,"+
                    "jumped INT DEFAULT 0"+
                    ")";




    String SQL_POPULATE_WIFI_LOCATION =
            "INSERT INTO " + SQL_LOCATION + " (location_id,name) VALUES " +
                    "(1,'location 1')," +
                    "(2,'location 2')," +
                    "(3,'location 3')," +
                    "(4,'location 4')," +
                    "(5,'location 5')," +
                    "(6,'location 6')";
    String SQL_POPULATE_WIFI_LOCATION_SSID =
            "INSERT INTO " + SQL_WIFI_LOCATION_SSID + " (location_id,ssid) VALUES " +
                    "(1,'SSID 1')," +
                    "(1,'SSID 2')," +
                    "(1,'SSID 3')," +
                    "(2,'SSID 2')," +
                    "(3,'WIFI_LOCATION 3')," +
                    "(4,'SSID 4')," +
                    "(5,'SSID 5')," +
                    "(6,'SSID 6')";

    String SQL_POPULATE_MESSAGES =
            "INSERT INTO " + SQL_MESSAGES+ " (message_id, content, author_id, location_id, time_start, time_end ) VALUES " +
            "(1,'small message','1',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"+
            "(6,'small message','1',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"+
            "(7,'small message','1',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"+
            "(8,'small message','1',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"+
            "(2,'small message','Big User Name, Its Big, Its Huge, Why am I doing this, it just hurts.. :(',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"+
            "(3,'small message','1',5,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"+
            "(4,'A really big message, the biggest of message, it must be good to shoot the mother of all bombs and then go on vacation to florida on a Thursday, at 4pm','1',1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),"+
            "(5,'A really big message, the biggest of message, it must be good to shoot the mother of all bombs and then go on vacation to florida on a Thursday, at 4pm Aparently not big enough so we ought make it go out of the screen if really are testing for everything, at least I am not missing much, maybe save message as read in the database :D','Big User Name, Its Big, Its Huge, Why am I doing this, it just hurts.. :(',5,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";


    String SQL_DELETE_LOCATION=
            "DROP TABLE IF EXISTS " + SQL_LOCATION;
    String SQL_DELETE_WIFI_LOCATION_SSID=
            "DROP TABLE IF EXISTS " + SQL_WIFI_LOCATION_SSID;
    String SQL_DELETE_GPS_LOCATION=
            "DROP TABLE IF EXISTS " + SQL_GPS_LOCATION;
    String SQL_DELETE_LOGIN =
            "DROP TABLE IF EXISTS " + SQL_LOGIN;
    String SQL_DELETE_KEYWORDS=
            "DROP TABLE IF EXISTS " + SQL_KEYWORDS;
    String SQL_DELETE_USER_KEYWORDS=
            "DROP TABLE IF EXISTS " + SQL_USER_KEYWORDS;
    String SQL_DELETE_MESSAGES=
            "DROP TABLE IF EXISTS " + SQL_MESSAGES;
    String SQL_DELETE_READ_MESSAGES=
            "DROP TABLE IF EXISTS " + SQL_READ_MESSAGES;
    String SQL_DELETE_WIFI_MESSAGES=
            "DROP TABLE IF EXISTS " + SQL_WIFI_MESSAGES;

}
