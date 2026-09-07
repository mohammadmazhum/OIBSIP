# Train Reservation System (Java Swing + JDBC + SQLite)

A complete GUI-based train reservation system covering every item on the
Task 1 checklist: login, book a ticket (with train auto-lookup and PNR
generation), confirmation dialog, and cancel a ticket by PNR (with a
confirm dialog before deleting).

## Files
```
src/
  Main.java              - entry point
  DBConnection.java      - creates/seeds the SQLite database on first run
  LoginForm.java          - username + password login screen
  MainMenu.java            - hub after login (Book / Cancel / Logout)
  ReservationForm.java     - booking form + PNR generation + confirmation
  CancellationForm.java    - PNR lookup + "are you sure?" + delete
lib/
  sqlite-jdbc.jar         - JDBC driver (already included, no setup needed)
```

## How to run

You need a JDK installed (Java 8+). No MySQL/XAMPP setup needed — SQLite
just creates a local file called `reservation.db` automatically.

**1. Compile:**
```
javac -cp lib/sqlite-jdbc.jar -d build src/*.java
```

**2. Run:**
```
java -cp build:lib/sqlite-jdbc.jar Main
```
(On Windows, use a semicolon instead of a colon:
`java -cp build;lib/sqlite-jdbc.jar Main`)

A `reservation.db` file will be created in your current folder the first
time you run it, pre-loaded with a login and 4 sample trains.

## Default login
```
username: admin
password: admin123
```
(Stored in the `users` table — you can add more rows there later, or add
a "Sign Up" form yourself as an extension.)

## Sample train numbers to test the "Lookup Train" button
| Train No | Name              | Route                    |
|----------|-------------------|---------------------------|
| 12001    | Shatabdi Express  | New Delhi -> Bhopal       |
| 12951    | Mumbai Rajdhani   | Mumbai -> New Delhi       |
| 12628    | Karnataka Express | Bangalore -> New Delhi    |
| 16022    | Kaveri Express    | Bangalore -> Chennai      |

## Checklist coverage
- [x] Login form: username + password; "Access Denied" JOptionPane for bad credentials
- [x] Reservation form: passenger name, train number, train name/source/destination
      auto-populated via "Lookup Train" button, class dropdown, date field
- [x] Book button inserts into SQLite and generates a unique 6-digit PNR
- [x] Confirmation dialog shown after successful booking
- [x] Cancellation form: PNR field + Fetch button shows full booking details
- [x] Confirm cancellation dialog ("Are you sure?") before deleting
- [x] Validation: empty-field checks, numeric train number/PNR, yyyy-mm-dd date
      format check, and rejects past dates

## Notes / things you may want to extend for extra credit
- Passwords are stored in plain text for simplicity — for a stronger version,
  hash them (e.g. with `MessageDigest` SHA-256) before storing/comparing.
- Currently cancellation *deletes* the row per the spec ("removes the booking
  from the database"). If your instructor wants history kept, change the
  DELETE in `CancellationForm.java` to an `UPDATE reservations SET status =
  'CANCELLED' WHERE pnr = ?` instead.
- To switch to MySQL instead of SQLite: change the JDBC URL in
  `DBConnection.java` to `jdbc:mysql://localhost:3306/yourdb`, swap the
  driver jar, and add a username/password to `DriverManager.getConnection`.
