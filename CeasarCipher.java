/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ceasarcipher;

import java.util.Scanner;
import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 *
 * @author jenniferli
 */
public class CeasarCipher {

    /**
     * Shifts the letters in a message and returns a decoded(shift backwards) or
     * encoded(move forward) String. This method is used when 'e' or 'd' is
     * inputted when asked what action the user would like to perform.
     *
     * @param messageIn the message to encode/decode
     * @param shift number of intervals to shift back/forward
     * @return encoded/decoded message
     */
    private static String changeIt(String messageIn, int shift) {//new string shifted forwards
        String encodeMessage = "";
        char encodedChar = 'a';
        int charIndex = 0;

        //loops while theres still characters left in the message to decode/encode
        for (int i = 0; i < messageIn.length(); i++) {
            //if the current character being analyzed isn't a letter keep it the same
            if (Character.isLetter(messageIn.charAt(i))) {
                //if the current character is capital set the corresponding ascii index
                if (Character.isUpperCase(messageIn.charAt(i))) {
                    charIndex = 65;

                } else {//otherwise set it to the lowercase ascii index
                    charIndex = 97;
                }
                //set the current character being analyzed to a general index(0-26) in the alphabet
                char letter = (char) (messageIn.charAt(i) - charIndex);
                //find the position of the decodeed/encoded character within the alphabet
                int remainder = ((letter + shift) + 26) % 26;
                //add the position of the decoded/encoded character to the start of the corresponding alphabet(capital or lowercase)
                encodedChar = (char) (charIndex + remainder);
            } else {
                encodedChar = messageIn.charAt(i);
            }
            //add the current decodeed/encoded character to the final decoded/encoded message 
            encodeMessage += encodedChar;
        }

        //returns the encoded/decoded message
        return encodeMessage;

    }

    /**
     * Shifts the letters in a message, finds all decoded possibilities. This
     * method is used when 'b' is inputted when asked what action the user would
     * like to perform.
     *
     * @param message the message to decode
     * @return a decoded message with common english words.
     */
    private static String[] breakCode(String message) {
        //create a list for all the possible decoded messages
        String[] possibleMessages = new String[26];

        //loops through each possible shift interval
        for (int j = 0; j <= 25; j++) {
            //set the current shift interval
            int interval = j;

            //decoded message
            String decodedMessage = "";

            //current decoded character
            char decodedChar = 'a';

            //starting character (depending on capital/lower case letters)
            int charIndex = 0;

            //loops while theres still characters left in the message to decode
            for (int i = 0; i < message.length(); i++) {
                //if the current character being analyzed isn't a letter keep it the same
                if (Character.isLetter(message.charAt(i))) {
                    //if the current character is capital set the corresponding ascii index
                    if (Character.isUpperCase(message.charAt(i))) {
                        charIndex = 65;

                    } else {//otherwise set it to the lowercase ascii index
                        charIndex = 97;
                    }
                    //set the current character being analyzed to a general index(0-26) in the alphabet
                    char letter = (char) (message.charAt(i) - charIndex);
                    //find the position of the decoded character within the alphabet
                    int remainder = ((letter + interval) + 26) % 26;
                    //add the position of the decoded character to the start of the corresponding alphabet(capital or lowercase)
                    decodedChar = (char) (charIndex + remainder);
                } else {
                    decodedChar = message.charAt(i);
                }
                //add the current decodeed character to the final decoded message 
                decodedMessage += decodedChar;
            }
            //add the current decoded message to the list
            possibleMessages[j] = decodedMessage;
        }
        //returns the list of possible messages
        return possibleMessages;
    }

    /**
     * determines the most likely message by counting the number of
     * words that match a list of 1003 most common english words in the 
     * decoded messages.
     *
     * @param messages[] list of possible decoded messages
     * @return The key and sentence of the message with the most english words
     */
    private static String bestMessage(String[] messages) {
        //get the HashSet of 1003 most common english words
        HashSet<String> compareList = loadWords();
        //returned message, that has common english words
        String best = "";
        //checks the length of the message
        int check = 0;

        //loops through the list of decoded messages
        int mostMatchesIndex = 0;
        int mostMatches = -1;
        for (int i = 0; i <= 25; i++) {
            //create a list that contains the words in the decoded message 
            StringTokenizer split = new StringTokenizer(messages[i]);
            String[] messageTokenized = new String[split.countTokens()];
            for (int h = 0; split.hasMoreElements(); h++) {
                messageTokenized[h] = split.nextToken();
            }

            //sets the conditon for the loop below
            //if the message only contains one word set the condition to one, so it loops once
            if (messageTokenized.length == 1) {
                check = 1;
            } else {
                check = messageTokenized.length - 1;
            }

            int currentMatches = 0;
            //loops through all of the words in the current decoded message
            for (int j = 0; j < check; j++) {
                //if the decoded message contains an english word from the 
                //1003 most common words list return the decoded message
                if (compareList.contains(messageTokenized[j])) {
                    currentMatches++;
                }
            }
            if (currentMatches > mostMatches) {
                mostMatchesIndex = i;
                mostMatches = currentMatches;
            }
        }

        //returns the best decoded message
        best = ("The best decode key was " + mostMatchesIndex + "\n Decoded message is: " + messages[mostMatchesIndex]);
        return best;
    }

    /**
     * Creates a HashSet from a written file of the 1003 most common english
     * words. This method is used when the breakCode() method needs to reference
     * english words to determine the best decoded message.
     *
     * @return HashSet of 1003 most common english words
     */
    private static HashSet loadWords() {
        //create a string HashSet
        HashSet<String> wordsHashSet = new HashSet<String>();;
        try {
            //take in the byte values from the 1-1000 (common words) file
            byte[] fileInBytes = Files.readAllBytes(Paths.get("1-1000.txt"));
            //convert the byte values of the common words to a string
            String wordListContents = new String(fileInBytes, "UTF-8");

            //create a list of words (all the common words tokenized)
            String[] wordsTokenized = wordListContents.split("\n");

            //adds each tokenized common word to the HashSet
            for (String word : wordsTokenized) {
                wordsHashSet.add(word);
            }
        } catch (IOException e) {

        }
        //returns the hashSet
        return wordsHashSet;

    }

    /**
     * Calls the decode/encode methods, asks the user for input, checks for
     * exceptions
     *
     * @return the encoded/decoded message(s) or ends the program
     */
    public static void main(String[] args) {
        //keyboard listener
        Scanner keyboard = new Scanner(System.in);

        //outputed encoded/decoded messages
        String result = "";

        //shift interval for decode/encode function
        int interval;

        //checks if the shift interval is between 0-25
        boolean shiftIntervalCheck = false;

        //output user instructions
        System.out.println("Enter 'e' to Encode your message");
        System.out.println("Enter 'd' to Decode your message");
        System.out.println("Enter 'b' to BruteForce(automatically decode) your message");
        System.out.println("Enter 'q' to quit the program");

        boolean play = false;
        char action = 'a';
        do {
            try {
                action = keyboard.nextLine().charAt(0);
                //checks if the inputed action is a valid character
                if (action == 'e' || action == 'd' || action == 'b' || action == 'q') {
                    play = true;
                } else {
                    System.out.println("You didn't enter a valid character. Please try again.");
                }
            } catch (StringIndexOutOfBoundsException e) {
                //repeats if a the user doesn't input anything (empty string)
                System.out.println("no input was entered please enter 'e', 'd', 'b', or 'q'.");
            }
        } while (!play);
        //displays the entered action
        System.out.println("You've entered: " + action);

        //repeat program till user tells the program to quit
        while (action != 'q' && play == true) {
            //take in message to decode/encode
            System.out.println("What is your message: ");
            String message = keyboard.nextLine();

            //call the corresponding methods
            switch (action) {
                case 'e':
                    //takes in the interval of shifts
                    System.out.println("how many intervals do you want to move by:");
                    do {
                        interval = keyboard.nextInt();
                        //checks if the shift is within bounds, asks for user input again if not
                        if (interval > 0 && interval < 25) {
                            shiftIntervalCheck = true;
                        } else {
                            System.out.println("Please input an interval less than 25 and more than 0");
                        }
                    } while (!shiftIntervalCheck);

                    keyboard.nextLine();//gunk (nextInt()/nextLine() interference)

                    //calls the decode/encode message, with a possitive shift interval
                    result = changeIt(message, interval);
                    //returns the decoded/encoded message
                    System.out.printf("This is your encoded message: \n %s \n", result);
                    break;
                case 'd':
                    //takes in the interval of shifts
                    System.out.println("how many intervals do you want to move by:");

                    do {
                        interval = keyboard.nextInt();
                        //checks if the shift is within bounds, asks for user input again if not
                        if (interval > 0 && interval < 25) {
                            shiftIntervalCheck = true;
                        } else {
                            System.out.println("Please input an interval less than 25 and more than 0");
                        }
                    } while (!shiftIntervalCheck);

                    keyboard.nextLine();//gunk (nextInt()/nextLine() interference)

                    //calls the decode/encode message, with a possitive shift interval
                    result = changeIt(message, -interval);
                    //returns the decoded/encoded message
                    System.out.printf("This is your decoded message: \n %s \n", result);
                    break;
                case 'b':
                    //calls the breakCode function
                    String[] bCode = breakCode(message);
                    result = bestMessage(bCode);
                    //returns the decoded message with common english words
                    System.out.println("These are all the possible messages:");
                    //list of all possible decoded messages
                    for (int i = 0; i < 25; i++) {
                        System.out.println("For shift of " + i + ", decoded is: " + bCode[i]);
                    }
                    //best decoded message from list and the key
                    System.out.println(result);
                    break;
            }
            //ask for the next action
            System.out.println("Enter 'e' to Encode your message");
            System.out.println("Enter 'd' to Decode your message");
            System.out.println("Enter 'b' to BruteForce(automatically decode) your message");
            System.out.println("Enter 'q' to quit the program");

            //checks for errors (same as lines 215-228) 
            play = false;
            do {
                try {
                    action = keyboard.nextLine().charAt(0);
                    if (action == 'e' || action == 'd' || action == 'b' || action == 'q') {
                        play = true;
                    } else {
                        System.out.println("You didn't enter a valid character. Please try again.");
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("no input was entered please enter 'e', 'd', 'b', or 'q'.");
                }
            } while (!play);
            System.out.println("You've entered: " + action);

        }
        //once 'q' is entered, thanks for playing is displayed
        System.out.println("Thanks for playing.");
    }

}
