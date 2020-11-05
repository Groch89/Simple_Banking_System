package banking;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void menu() {                             // main method, with menu and communication with users
        boolean isRunning = true;
        while (isRunning) {                                 // run menu as long, as long isRunning is set to true
            System.out.print("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit\n");

            int usersChoice = scanner.nextInt();            // var stores users choice
            System.out.println();

            switch (usersChoice) {                          // depending on option user can:

                case 1:                                     // if pressed 1 - make new account
                    Card usersAccount = new Card();         // make a new card

                    System.out.println("Your card has been created\n" +
                            "Your card number:\n" +
                            usersAccount.getCardNumber() + "\n" +           // get cards number
                            "Your card PIN:\n" +
                            usersAccount.getPin() + "\n");                  // and corresponding pin

                    /*
                     *   Below, after creation od card object, we are inserting values into database
                     *   using insert(id, card number and pin) method.
                     *   Our database can already contain a few records, so we call getNextFreeId() method, which returns
                     *   us "max id + 1".
                     *   usersAccount.getCardNumber() and usersAccount.getPin() are self-explanatory.
                     *   we use them to get cards number and pin and insert them to DB
                     */

                    DbOperations.insert(DbOperations.getNextFreeId(), usersAccount.getCardNumber(), usersAccount.getPin());
                    break;

                case 2:                                     // case 2, where user can try to sign in
                    System.out.println("Enter your card number:");
                    String inputtedCardNumber = scanner.next();
                    System.out.println("Enter your PIN:");
                    String inputtedPin = scanner.next();

                    int id = DbOperations.selectCardNumberAndReturnId(inputtedCardNumber);
                    // ^we try to find in DB this card number and get corresponding id. If there is no card found, return -1 as ID

                    if (id < 0) {                                                   // no card found
                        System.out.println("Wrong card number or PIN!\n");
                    } else {                                                        // card found. check if Pin is correct
                        if (inputtedPin.equals(DbOperations.getPinFromId(id))) {    // compare inputted pin and pin from card (using cards id)
                            System.out.println("You have successfully logged in!\n");
                            boolean isExitedFromLoginPage = logIn(id);              // we are now going to log in using id.
                            if (isExitedFromLoginPage) {                            // Depending of users choice from logged in menu,
                                isRunning = false;                                  // we can continue this switch loop, or exit
                            }
                        } else {
                            System.out.println("Wrong card number or PIN!\n");
                        }
                    }
                    break;

                case 0:
                    isRunning = false;
                    System.out.println("Bye!\n");
                    break;

                default:
                    System.out.println("Wrong input! Please try again.\n");
            }
        }
    }

    public static boolean logIn(int id) {               // log in using id
        boolean isLoggedIn = true;                      // loop as long as this var is set to true
        boolean isExited = false;                       // this var is used to let know if user whats to log out or force quited

        while (isLoggedIn) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");

            int usersChoice = scanner.nextInt();
            System.out.println("");

            switch (usersChoice) {
                case 1:                                                     // case 1: show balance  ******************************************
                    int balance = DbOperations.getBalanceFromDbUsingId(id); // use id to get balance from db
                    System.out.println("Balance: " + balance + "\n");       // and show it to user
                    break;
                case 2:                                                     // case 2: add income    ******************************************
                    System.out.println("Enter income:");
                    int income = scanner.nextInt();
                    DbOperations.addCashToCard(income, id);                 // add specified amount of cash, to account with corresponding id
                    System.out.println("Income was added!\n");
                    break;
                case 3:                                                     // case 3: do transfer   ******************************************
                    System.out.println("Transfer");
                    System.out.println("Enter card number:");
                    String cardNumber = scanner.next();

                    Card template = new Card(cardNumber);

                    boolean passedLuhnAlgorithm = template.passedLuhnAlgorithm();

                    if (passedLuhnAlgorithm) {
                        int idDestinationsCard = DbOperations.selectCardNumberAndReturnId(cardNumber);
                        if (idDestinationsCard < 0) {
                            System.out.println("Such a card does not exist.\n");
                        } else if(idDestinationsCard == id) {
                            System.out.println("You can't transfer money to the same account!\n");
                        } else {
                            System.out.println("Enter how much money you want to transfer:");
                            int amountToTransfer = scanner.nextInt();

                            if (amountToTransfer > DbOperations.getBalanceFromDbUsingId(id)) {
                                System.out.println("Not enough money!\n");
                            } else {
                                DbOperations.addCashToCard(amountToTransfer, idDestinationsCard);
                                DbOperations.addCashToCard(-amountToTransfer, id);
                                System.out.println("Success!\n");
                            }
                        }
                    } else {
                        System.out.println("Probably you made mistake in the card number. Please try again!\n");
                    }
                    break;

                case 4:                                                     // case 4: close account ******************************************
                    DbOperations.deleteAccount(id);
                    System.out.println("The account has been closed!\n");
                    isLoggedIn = false;
                    break;
                case 5:                                                     // case 5: to log out    ******************************************
                    isLoggedIn = false;                                     //set to false to terminate while loop
                    System.out.println("You have successfully logged out!\n");  // isExited remains false,
                    break;                                                  // so we just sign out, not quiting

                case 0:                                                     // case 0: used to exit from whole application  *******************
                    isExited = true;                                        // used to let know that we're not logging out, but quitting
                    isLoggedIn = false;                                     // set false to terminate while loop
                    break;
                default:
                    System.out.println("Wrong input. Please try again.\n");
                    break;
            }
        }
        return isExited;    // return this value, to let know program if we are just logged out (false) or quited (true)
    }

    public static String urlFromCommandLine(String[] args) {        // returns database filename passed as argument
        String fileName = "";

        if (args.length != 0) {                                     // do loop only if there are arguments passed
            for (int i = 0; i < args.length - 1; i += 2) {
                if ("-fileName".equals(args[i])) {                  // if -fileName found, then next argument contains name of file
                    fileName = args[i + 1];
                }
            }
        }
//        System.out.println("uwaga podaje url: " + "jdbc:sqlite:".concat(fileName));
        return "jdbc:sqlite:".concat(fileName);
    }

    public static void main(String[] args) {

        DbOperations.setUrl(urlFromCommandLine(args));          // setUrl/filename for database using passed arguments
        DbOperations.createTable();                             // create DB table
        menu();                                                 // shows menu
    }
}
