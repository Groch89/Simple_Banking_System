package banking;

import org.sqlite.SQLiteDataSource;
import java.sql.*;

public class DbOperations {

    private static final SQLiteDataSource dataSource = new SQLiteDataSource();

    protected static void setUrl (String url) {
        dataSource.setUrl(url);
    }

    protected static void insert(int id, String number, String pin) {

        String sql = "INSERT INTO card(id, number, pin) VALUES (?,?,?)";
//        System.out.println("To dodajemy do DB " + id + ", number:  " + number + ". pin:   " + pin);

        try (Connection connection = connect();
             PreparedStatement prepStat = connection.prepareStatement(sql)) {
            prepStat.setInt(1, id);
            prepStat.setString(2, number);
            prepStat.setString(3, pin);
            prepStat.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static int selectCardNumberAndReturnId(String cardNumber) {
        // a co jak wpadnei numer ktorego nie ma? NO CO KURTWA WTEDY


        String sql = "SELECT id FROM card WHERE number = ?";
//        System.out.println("Tego nr szukamy w tabeli " + cardNumber);

        try (Connection connection = connect();
             PreparedStatement prepStat = connection.prepareStatement(sql)) {

//            System.out.println("bP 1");
            prepStat.setString(1, cardNumber);

//            System.out.println("bP 2");

            ResultSet rs = prepStat.executeQuery();
//            System.out.println("bP 3");

            while (rs.next()) {
//                System.out.println("Taki Id znalazlem dla tego nr konta " + rs.getInt("id"));
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("ERRRRROR KURWA  " + e.getMessage());
        }

//        System.out.println("gowno znalazlem, zwracam -1");
        return -1;
    }

    protected static String getPinFromId(int id) {
        String sql = "SELECT pin FROM card WHERE id = ?";

//        System.out.println("szukam pin dla tego ID " + id);

        try (Connection connection = connect();
             PreparedStatement prepStat = connection.prepareStatement(sql)) {

//            System.out.println("BP1");
            prepStat.setInt(1, id);

//            System.out.println("BP1 2");
            ResultSet rs = prepStat.executeQuery();

//            System.out.println("BP1 3");
            while (rs.next()) {
//                System.out.println("taki pin znalazlem: " + rs.getString("pin"));
                return rs.getString("pin");
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//        System.out.println("gowno znalalzem, wiec zwracam ZERO slownie");
        return "zero";
    }

    protected static int getBalanceFromDbUsingId(int id) {

        String sql = "SELECT balance FROM card WHERE id = ?";
//        System.out.println("szukam balance dla tego ID " + id);

        try (Connection connection = connect();
             PreparedStatement prepStat = connection.prepareStatement(sql)) {

//            System.out.println("Balance   # 1");
            prepStat.setInt(1, id);

//            System.out.println("Balance   # 2");
            ResultSet rs = prepStat.executeQuery();

//            System.out.println("Balance   # 3");
            while (rs.next()) {
//                System.out.println("taki balance znalazlem: " + rs.getInt("balance"));
                return rs.getInt("balance");
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//        System.out.println("nie znalazlem pinu wiec oddaje -1");
        return -1;
    }

    protected static int getMaxId() {
        String sql = "SELECT MAX(id) FROM card";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            return rs.getInt(1) + 1;
        } catch (SQLException e) {
            System.out.println("Error jakis :< :" + e.getMessage());
        }

//        System.out.println("wyszedl error i musze zwrocic zero :( getMaxId");
        return 0;
    }

    private static Connection connect() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    protected static void createTable() {

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(\n"
                        + "id INTEGER NOT NULL PRIMARY KEY,\n"
                        + "number TEXT NOT NULL,\n"
                        + "pin TEXT NOT NULL,\n"
                        + "balance INTEGER DEFAULT 0\n"
                        + ");");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
