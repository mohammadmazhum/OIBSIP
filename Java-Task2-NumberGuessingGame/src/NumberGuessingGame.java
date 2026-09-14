import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Console-based Number Guessing Game.
 *
 * The computer picks a random number within a range decided by the chosen
 * difficulty, and the player has a limited number of attempts to guess it,
 * receiving "Too High!" / "Too Low!" hints after every guess.
 */
public class NumberGuessingGame {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    // Keeps a "Round X — guessed in Y attempts" style summary for every
    // round played in this session, shown at the very end.
    private static final List<String> roundSummaries = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" WELCOME TO THE NUMBER GUESSING GAME");
        System.out.println("=========================================");

        boolean playAgain = true;
        int roundNumber = 1;

        while (playAgain) {
            System.out.println("\n--- Round " + roundNumber + " ---");
            Difficulty difficulty = chooseDifficulty();
            playRound(roundNumber, difficulty);
            roundNumber++;

            playAgain = askYesNo("Play again? (y/n): ");
        }

        printFinalSummary();
        System.out.println("\nThanks for playing! Goodbye.");
        scanner.close();
    }

    /** Represents the three difficulty tiers required by the feature checklist. */
    private enum Difficulty {
        EASY(1, 50, 10),
        MEDIUM(1, 100, 7),
        HARD(1, 200, 5);

        final int min;
        final int max;
        final int maxAttempts;

        Difficulty(int min, int max, int maxAttempts) {
            this.min = min;
            this.max = max;
            this.maxAttempts = maxAttempts;
        }
    }

    /** Prompts the user to pick a difficulty level, validating the input. */
    private static Difficulty chooseDifficulty() {
        while (true) {
            System.out.println("Choose a difficulty:");
            System.out.println("  1. Easy   (1-50,  10 attempts)");
            System.out.println("  2. Medium (1-100,  7 attempts)");
            System.out.println("  3. Hard   (1-200,  5 attempts)");
            System.out.print("Enter 1, 2, or 3: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": return Difficulty.EASY;
                case "2": return Difficulty.MEDIUM;
                case "3": return Difficulty.HARD;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.\n");
            }
        }
    }

    /** Runs a single round: generate a number, loop guesses until correct or out of attempts. */
    private static void playRound(int roundNumber, Difficulty difficulty) {
        int target = difficulty.min + random.nextInt(difficulty.max - difficulty.min + 1);
        int attempts = 0;
        boolean guessedCorrectly = false;

        System.out.println("I'm thinking of a number between " + difficulty.min +
                " and " + difficulty.max + ". You have " + difficulty.maxAttempts + " attempts.");

        while (attempts < difficulty.maxAttempts) {
            int guess = readValidGuess(difficulty.min, difficulty.max, difficulty.maxAttempts - attempts);
            attempts++;

            if (guess == target) {
                System.out.println("Correct! You guessed it in " + attempts + " attempt(s).");
                guessedCorrectly = true;
                break;
            } else if (guess < target) {
                System.out.println("Too Low! Attempts remaining: " + (difficulty.maxAttempts - attempts));
            } else {
                System.out.println("Too High! Attempts remaining: " + (difficulty.maxAttempts - attempts));
            }
        }

        if (!guessedCorrectly) {
            System.out.println("You Lost! The number was " + target + ".");
            roundSummaries.add("Round " + roundNumber + " — did not guess (limit " +
                    difficulty.maxAttempts + " reached), number was " + target);
        } else {
            roundSummaries.add("Round " + roundNumber + " — guessed in " + attempts + " attempts");
        }
    }

    /**
     * Reads a guess from the user, validating that it is a numeric value
     * within the current difficulty's range. Re-prompts on invalid input
     * without consuming one of the player's attempts.
     */
    private static int readValidGuess(int min, int max, int attemptsLeft) {
        while (true) {
            System.out.print("Enter your guess (" + attemptsLeft + " attempt(s) left): ");
            String input = scanner.nextLine().trim();

            try {
                int guess = Integer.parseInt(input);
                if (guess < min || guess > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return guess;
            } catch (NumberFormatException e) {
                System.out.println("That's not a valid number. Please try again.");
            }
        }
    }

    /** Prompts a yes/no question and keeps asking until valid input is given. */
    private static boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please answer with 'y' or 'n'.");
        }
    }

    /** Prints the "Round X — guessed in Y attempts" summary for every round played. */
    private static void printFinalSummary() {
        System.out.println("\n=========================================");
        System.out.println(" SESSION SUMMARY");
        System.out.println("=========================================");
        for (String summary : roundSummaries) {
            System.out.println(summary);
        }
    }
}
