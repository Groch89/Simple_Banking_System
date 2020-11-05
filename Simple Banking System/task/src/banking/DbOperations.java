package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class DbOperations {

    private static final SQLiteDataSource dataSource = new SQLiteDataSource();

    protected static void setUrl(String url) {             // used to set DB's URL with passed run argument
        dataSource.setUrl(url);
    }

    private static Connection connect() {                   // connects to DB, and returns connection as Object
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    protected static void insert(int id, String number, String pin) {       // adding new record to DB

        String sql = "INSERT INTO card(id, number, pin) VALUES (?,?,?)";    // some sql magic

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

    protected static int selectCardNumberAndReturnId(String cardNumber) {       // look for this card number, and return id when found, or -1 otherwise
        String sql = "SELECT id FROM card WHERE number = ?";
        try (Connection connection = connect();
             PreparedStatement prepStat = connection.prepareStatement(sql)) {

            prepStat.setString(1, cardNumber);
            ResultSet rs = prepStat.executeQuery();

//            while (rs.next()) {
//                System.out.println("Taki Id znalazlem dla tego nr konta " + rs.getInt("id"));
                return rs.getInt("id");
//            }
        } catch (SQLException e) {
            System.out.println("ERRRRROR: " + e.getMessage());
            return -1;
        }
    }

    protected static String getPinFromId(int id) {
        String sql = "SELECT pin FROM card WHERE id = ?";

//        System.out.println("szukam pin dla tego ID " + id);

        try (Connection connection = connect();
             PreparedStatement prepStat = connection.prepareStatement(sql)) {

            prepStat.setInt(1, id);
            ResultSet rs = prepStat.executeQuery();

//            while (rs.next()) {
//                System.out.println("taki pin znalazlem: " + rs.getString("pin"));
            return rs.getString("pin");
//            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//        System.out.println("nic nie znalalzem, wiec zwracam ZERO slownie");           // na potrzeby testow
        return "zero";
    }

    protected static int getBalanceFromDbUsingId(int id) {

        String sql = "SELECT balance FROM card WHERE id = ?";
//        System.out.println("szukam balance dla tego ID " + id);

        try (Connection connection = connect();
             PreparedStatement prepStat = connection.prepareStatement(sql)) {

            prepStat.setInt(1, id);
            ResultSet rs = prepStat.executeQuery();

//            while (rs.next()) {
//                System.out.println("taki balance znalazlem: " + rs.getInt("balance"));
            return rs.getInt("balance");
//            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

//        System.out.println("nie znalazlem pinu wiec oddaje -1");           // na potrzeby testow
        return -1;
    }

    protected static void addCashToCard(int amount, int id) {
        String sql = "UPDATE card SET balance = balance + ? WHERE id = ?";

        try (Connection connection = connect();
        PreparedStatement prepStm = connection.prepareStatement(sql)) {

            prepStm.setInt(1, amount);
            prepStm.setInt(2, id);
            prepStm.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    protected static int getNextFreeId() {
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

    protected static void deleteAccount(int id) {
        String sql = "DELETE FROM card WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement prepStm = connection.prepareStatement(sql)) {

            prepStm.setInt(1, id);
            prepStm.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    protected static void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS card(\n"
                + "id INTEGER NOT NULL PRIMARY KEY,\n"   // TODO: change to AUTOINCREMENT and UNIQUE?
                + "number TEXT NOT NULL,\n"
                + "pin TEXT NOT NULL,\n"
                + "balance INTEGER DEFAULT 0 );\n";

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
