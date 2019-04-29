package common;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SQLHelper {
    private static final String SQL_SCRIPT = "src/main/sql/database.sql";

    public static void executeSetUpScript() {
        Connection connection;
        Statement statement;
        final String delimiter = ";";
        try {
            Class.forName(Constants.DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DATABASE_CONNECTION);
            statement = connection.createStatement();
            Scanner scan = new Scanner(new File(SQL_SCRIPT));
            scan.useDelimiter(Pattern.compile(delimiter));
            String line;
            // read script line by line
            while(scan.hasNext()){
                line = scan.next();
                line += delimiter;
                statement.execute(line);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
