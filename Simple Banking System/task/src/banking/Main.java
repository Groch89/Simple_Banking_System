package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static String url;

    private static final Scanner scanner = new Scanner(System.in);
    private static final SQLiteDataSource dataSource = new SQLiteDataSource();

    public static void insert(int id, String number, String pin) {

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

    public static int selectCardNumberAndReturnId(String cardNumber) {
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

    public static String getPinFromId(int id) {
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

    public static int getBalanceFromDbUsingId(int id) {

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

    public static boolean logIn(int id) {
        boolean isLoggedIn = true;
        boolean isExited = false;

        while (isLoggedIn) {
            System.out.println("1. Balance");
            System.out.println("2. Log out");
            System.out.println("0. Exit");

            int usersChoice = scanner.nextInt();

            switch (usersChoice) {
                case 1:
                    int balance = getBalanceFromDbUsingId(id);
                    System.out.println("Balance: " + balance + "\n");
                    break;
                case 2:
                    isLoggedIn = false;
                    System.out.println("You have successfully logged out!\n");
                    break;
                case 0:
                    isExited = true;
                    isLoggedIn = false;
                    break;
                default:
                    System.out.println("Wrong input. Please try again.");
                    break;
            }
        }
        return isExited;
    }

    public static void menu() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.print("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit\n");

            int usersChoice = scanner.nextInt();
            System.out.println();

            switch (usersChoice) {

                case 1:
                    Card usersAccount = new Card();

                    System.out.println("Your card has been created\n" +
                            "Your card number:\n" +
                            usersAccount.getCardNumber() + "\n" +
                            "Your card PIN:\n" +
                            usersAccount.getPin() + "\n");

                    insert(getMaxId(), usersAccount.getCardNumber(), usersAccount.getPin());
                    break;

                case 2:
                    System.out.println("Enter your card number:");
                    String inputtedCardNumber = scanner.next();
                    System.out.println("Enter your PIN:");
                    String inputtedPin = scanner.next();

                    int id = selectCardNumberAndReturnId(inputtedCardNumber);

                    if (id < 0) {
                        System.out.println("Wrong card number or PIN!\n");
                    } else {
                        if (getPinFromId(id).equals(inputtedPin)) {
                            System.out.println("You have successfully logged in!\n");
                            boolean isExitedFromLoginPage = logIn(id);
                            if (isExitedFromLoginPage) {
                                isRunning = false;
                            }
                        } else {
                            System.out.println("Wrong card number or PIN!\n");
                        }
                    }
                    break;

                case 0:
                    isRunning = false;
                    System.out.println("Bye!");
                    break;

                default:
                    System.out.println("Wrong input! Please try again.");
            }
        }
    }

    private static int getMaxId() {
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

    public static String urlFromCommandLine(String[] args) {
        String fileName = "";

        if (args.length != 0) {
            for (int i = 0; i < args.length - 1; i = i + 2) {
                if ("-fileName".equals(args[i])) {
                    fileName = args[i + 1];
                }
            }
        }
//        System.out.println("uwaga podaje url: " + "jdbc:sqlite:".concat(fileName));
        return "jdbc:sqlite:".concat(fileName);
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

    private static void createTable() {

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

    public static void main(String[] args) {

        url = urlFromCommandLine(args);
        dataSource.setUrl(url);
        createTable();
        menu();
    }
}
