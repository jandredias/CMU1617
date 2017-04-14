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


    String[] SQL_LOCATION_COLUMNS = {
            "location_id",
            "name"
    };
    String[] SQL_WIFI_LOCATION_SSID_COLUMNS = {
            "location_id",
            "ssid"
    };
    String[] SQL_GPS_LOCATION_COLUMNS = {
            "location_id",
            "latitude",
            "longitude",
            "radius"
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


    String SQL_CREATE_LOCATION =
            "CREATE TABLE " + SQL_LOCATION + " (" +
            "location_id INT NOT NULL," +
            "name VARCHAR(255) )";
    String SQL_CREATE_WIFI_LOCATION_SSID =
            "CREATE TABLE " + SQL_WIFI_LOCATION_SSID + " (" +
            "location_id INT NOT NULL," +
            "ssid VARCHAR(255) )";
    String SQL_CREATE_GPS_LOCATION =
            "CREATE TABLE " + SQL_GPS_LOCATION + " (" +
                    "location_id INT UNIQUE NOT NULL," +
                    "latitude DOUBLE PRECISION,"+
                    "longitude DOUBLE PRECISION,"+
                    "radius INT" +
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

}
