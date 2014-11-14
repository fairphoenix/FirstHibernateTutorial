package org.hibernate.tutorial;

/**
 * Created by anatoliy on 13.11.2014.
 */
public class DataBaseLauncher {
    public static void main(String[] args) {
        org.hsqldb.Server.main(new String[]{"-database.0", "file:target/data/tutorial"});
    }
}
