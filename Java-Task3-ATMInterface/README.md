# ATM Interface (Java Console App, OOP Design)

A console-based ATM simulation with authentication, withdrawals, deposits,
transfers between accounts, and transaction history — built with clean
object-oriented design across 5 distinct classes.

## Classes
- `Account` — encapsulated account data (ID, PIN, balance, holder name, history)
- `Transaction` — a single logged transaction with timestamp
- `Bank` — manages all accounts, authentication, and transfers
- `ATM` — the interactive session/menu for one logged-in account
- `Main` — entry point, seeds sample accounts, handles login

## How to run

```
javac -d build src/*.java
java -cp build Main
```

## Sample accounts (seeded on startup)
| User ID | PIN  | Starting Balance |
|---------|------|-------------------|
| 1001    | 1234 | Rs. 5000.00       |
| 1002    | 5678 | Rs. 3000.00       |

## Checklist coverage
- [x] Startup prompt for User ID and PIN; denies access after 3 incorrect attempts
- [x] Main menu after login: Transaction History, Withdraw, Deposit, Transfer, Quit
- [x] Balance check before withdrawal/transfer; "Insufficient Funds" message shown
- [x] All transactions stored in an ArrayList (`Account.history`) and displayed
      clearly in Transaction History, with timestamps
- [x] 5 distinct Java classes: ATM, Account, Transaction, Bank, Main
- [x] Input validation: non-numeric/negative amounts rejected without side effects,
      invalid menu choices re-prompted, can't transfer to your own account or a
      non-existent account

## Notes
- Currency is shown as "Rs." rather than the ₹ symbol to avoid encoding issues
  across different terminals/IDEs.
- Transfers correctly credit the recipient account and log a matching
  TRANSFER-IN / TRANSFER-OUT pair in both accounts' histories.
