package cmu1617.andred.pt.locmess.Domain;

import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by Miguel on 08/04/2017.
 */

public class GPSLocation extends LocMessLocation {
    public GPSLocation(SQLDataStoreHelper dbHelper, String location_id) {
        super(dbHelper, location_id);
    }
}
