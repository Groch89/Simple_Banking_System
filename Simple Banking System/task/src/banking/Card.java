package banking;

import java.util.concurrent.ThreadLocalRandom;

public class Card {

    Card() {
        setCardNumber();
        setPin();
    }

    private final int[] cardNumber = new int[16];
    private final int[] BIN = {4, 0, 0, 0, 0, 0};
    private final int[] accountNumber = new int[9];
    private final int[] pin = new int[4];
    private int balance = 0;

    private void generateAccountNumber() {
        for (int i = 0; i < accountNumber.length; i++) {
            accountNumber[i] = ThreadLocalRandom.current().nextInt(0, 9 + 1);
        }
    }

    private int setChecksum(int[] originalCardNumberArray) {
        int[] copyOfCardNumberArray = originalCardNumberArray.clone();

        for (int i = 0; i < copyOfCardNumberArray.length - 1; i += 2) {
            copyOfCardNumberArray[i] = copyOfCardNumberArray[i] * 2;
        }

        for (int i = 0; i < copyOfCardNumberArray.length - 1; i++) {
            if (copyOfCardNumberArray[i] > 9)
                copyOfCardNumberArray[i] = copyOfCardNumberArray[i] - 9;
        }
        int sum = 0;

        for (int i = 0; i < copyOfCardNumberArray.length - 1; i++) {
            sum += copyOfCardNumberArray[i];
        }

        int rest = sum % 10;


        return rest == 0 ? 0 : 10 - rest;
    }

    private void setCardNumber() {
        generateAccountNumber();

        int currentIndex = 0;
        for (; currentIndex < BIN.length; currentIndex++) {
            cardNumber[currentIndex] = BIN[currentIndex];
        }
        for (int i = 0; i < accountNumber.length; i++) {
            cardNumber[currentIndex] = accountNumber[i];
            currentIndex++;
        }

        int checksum = setChecksum(cardNumber);

        cardNumber[currentIndex] = checksum;
    }

    private void setPin() {
        for (int i = 0; i < pin.length; i++) {
            pin[i] = ThreadLocalRandom.current().nextInt(0, 9 + 1);
        }
    }

    protected String getPin() {
        String PinAsString = "";
        for (int i = 0; i < pin.length; i++) {
            PinAsString = PinAsString.concat(String.valueOf(pin[i]));
        }
        return PinAsString;
    }

    protected String getCardNumber() {
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
