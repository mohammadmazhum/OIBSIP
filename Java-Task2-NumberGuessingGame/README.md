# Number Guessing Game (Java Console App)

A console-based game where the computer picks a random number and the player
tries to guess it within a limited number of attempts, receiving "Too High!"
/ "Too Low!" hints after each guess.

## How to run

```
javac -d build src/NumberGuessingGame.java
java -cp build NumberGuessingGame
```

## Checklist coverage
- [x] Random number generated at the start of each round
- [x] Guess input via Scanner (console)
- [x] "Too High!" / "Too Low!" / "Correct!" responses after each guess
- [x] Attempt counter shown before every guess
- [x] Maximum attempts limit — "You Lost!" message reveals the number if exceeded
- [x] "Play Again" prompt after each round
- [x] Score tracking across rounds: final "SESSION SUMMARY" shows
      "Round X — guessed in Y attempts" for every round played
- [x] (Bonus) Difficulty levels: Easy (1–50, 10 attempts), Medium (1–100, 7
      attempts), Hard (1–200, 5 attempts)
- [x] Input validation: non-numeric and out-of-range guesses are rejected
      without consuming an attempt; invalid difficulty choices are re-prompted
