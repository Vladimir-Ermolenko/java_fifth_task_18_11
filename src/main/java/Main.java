import java.sql.Connection;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean exit = false;
        while (!exit) {
            Scanner scanArg = new Scanner(System.in);
            System.out.println("Enter the date in (YYYY-MM-DD) format and the currency ID: ");
            String arg = scanArg.nextLine();
            try {
                String[] splitted = arg.split("\\s+");
                String date = "`" + splitted[0] + "`";
                String id = "'" + splitted[1] + "'";
                Connection conn = Db.connect("currencies", "root", "root");
                Db.searchDb(conn, date, id);
                System.out.println("");
                System.out.println("Would you like to get another exchange rate (Y/N) ?");
                Scanner scanMore = new Scanner(System.in);
                String again = scanMore.nextLine();
                if (Objects.equals(again, "N")) {
                    exit = true;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Please, provide us with valid data");
                System.out.println("");
                main(args);
                exit = true;
            }
        }
    }
}
