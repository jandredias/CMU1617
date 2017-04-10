package cmu1617.andred.pt.locmess;

/**
 * Created by andre on 07/01/17.
 */

public interface DataStore {

    String SQL_LOCATION = "wifi_location";
    String SQL_WIFI_LOCATION_SSID = "wifi_location_ssid";
    String SQL_GPS_LOCATION = "gps_location";



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
                    "location_id UNIQUE INT NOT NULL," +
                    "latitude DOUBLE PRECISION,"+
                    "longitude DOUBLE PRECISION,"+
                    "radius INT" +
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


}
