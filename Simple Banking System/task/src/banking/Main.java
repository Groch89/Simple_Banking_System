package banking;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

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
                    int balance = DbOperations.getBalanceFromDbUsingId(id);
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

                    DbOperations.insert(DbOperations.getMaxId(), usersAccount.getCardNumber(), usersAccount.getPin());
                    break;

                case 2:
                    System.out.println("Enter your card number:");
                    String inputtedCardNumber = scanner.next();
                    System.out.println("Enter your PIN:");
                    String inputtedPin = scanner.next();

                    int id = DbOperations.selectCardNumberAndReturnId(inputtedCardNumber);

                    if (id < 0) {
                        System.out.println("Wrong card number or PIN!\n");
                    } else {
                        if (DbOperations.getPinFromId(id).equals(inputtedPin)) {
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

    public static void main(String[] args) {

        DbOperations.setUrl(urlFromCommandLine(args));
        DbOperations.createTable();
        menu();
    }
}
