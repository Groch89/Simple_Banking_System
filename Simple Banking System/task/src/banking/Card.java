package banking;

import java.util.concurrent.ThreadLocalRandom;

public class Card {

    Card() {                        // after creating new Card object
        setCardNumber();            // set whole Card number
        setPin();                   // and PIN for it
    }

    private final int[] cardNumber = new int[16];                   // array to store complete card number (BIN + account Number + checksum)
    private final int[] BIN = {4, 0, 0, 0, 0, 0};                   // array to store BIN number (first 6 digits, for this project its constant 400000)
    private final int[] accountNumber = new int[9];                 // array to store account number (9 digits)
    private final int[] pin = new int[4];                           // array to store pin
    private int balance = 0;                                        // field holds accounts balance

    private void generateAccountNumber() {                          // method to create random account number
        for (int i = 0; i < accountNumber.length; i++) {            // (each element holds digit from 0 to 9, both inclusive)
            accountNumber[i] = ThreadLocalRandom.current().nextInt(0, 9 + 1);
        }
    }

    private int setChecksum(int[] originalCardNumberArray) {        // method takes array with card number, and sets Checksum for it, using Luhn algorithm
        int[] copyOfCardNumberArray = originalCardNumberArray.clone();      // operating on copy of array with card number

        for (int i = 0; i < copyOfCardNumberArray.length - 1; i += 2) {     // multiply odd digits by 2
            copyOfCardNumberArray[i] = copyOfCardNumberArray[i] * 2;
        }

        for (int i = 0; i < copyOfCardNumberArray.length - 1; i++) {        // subtract 9 from digits above 9
            if (copyOfCardNumberArray[i] > 9)
                copyOfCardNumberArray[i] = copyOfCardNumberArray[i] - 9;
        }

        int sum = 0;
        for (int i = 0; i < copyOfCardNumberArray.length - 1; i++) {        // sum all digits
            sum += copyOfCardNumberArray[i];
        }

        int rest = sum % 10;                                        // (sum + rest) have to be equal to multiple of 10
        return rest == 0 ? 0 : 10 - rest;                           // rest have to be in range 0-9, so if it's = 10, return 0
    }                                                               // otherwise return 10 - rest as our checksum

    private void setCardNumber() {                              // method to create whole card number
        generateAccountNumber();                                // first generate random account number (9 digits)

        int currentIndex = 0;                                   // currentIndex is index from Card Number (16 digits). We starts with 0
        for (; currentIndex < BIN.length; currentIndex++) {     // for both arrays BIC and Card Number
            cardNumber[currentIndex] = BIN[currentIndex];       // then copy from BIC to Card Number array (6 digits)
        }
        for (int i = 0; i < accountNumber.length; i++) {        // now we adding to card number array
            cardNumber[currentIndex] = accountNumber[i];        // digits from account number array
            currentIndex++;                                     // and incrementing card number arrays index
        }

        int checksum = setChecksum(cardNumber);                 // now after we got BIC + account number, we can set checksum for
                                                                // the whole card number
        cardNumber[currentIndex] = checksum;                    // and add it as the last digit to our card number array
    }

    private void setPin() {                             // set pin for card, 4 digits, using random digits in range 0-9, both inclusive
        for (int i = 0; i < pin.length; i++) {
            pin[i] = ThreadLocalRandom.current().nextInt(0, 9 + 1);
        }
    }

    protected String getPin() {                     // method returns PIN as String
        String PinAsString = "";
        for (int i = 0; i < pin.length; i++) {
            PinAsString = PinAsString.concat(String.valueOf(pin[i]));
        }
        return PinAsString;
    }

    protected String getCardNumber() {                  // method returns Card Number (BIC, account nr and checksum) as String
        String cardNumberAsString = "";
        for (int i = 0; i < cardNumber.length; i++) {
            cardNumberAsString = cardNumberAsString.concat(String.valueOf(cardNumber[i]));
        }
        return cardNumberAsString;
    }

    protected int getBalance() {
        return balance;
    }

}
