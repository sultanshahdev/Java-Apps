# FinanceTracker

A simple Java Swing + MySQL app to help you track your weekly pocket money using the 50-30-20 rule.

---

## How to Run

### 1. Set Up the Database

- Open MySQL and run the provided `FinanceTracker.sql` file to create the database and tables.

### 2. Add MySQL Connector/JAR

- Download the MySQL Connector/J `.jar` file.
- Make sure you add the path of this `.jar` file to your classpath when you compile and run the Java files.

### 3. Compile and Run

```sh
javac -cp ".:/path/to/mysql-connector-java-8.x.x.jar" FinanceTracker.java
java -cp ".:/path/to/mysql-connector-java-8.x.x.jar" FinanceTracker
```
*(On Windows, use `;` instead of `:` in the classpath)*

---

## Notes

- Make sure your MySQL username, password, and database in the code match your own setup.
- If you see an error about `com.mysql.cj.jdbc.Driver`, it means the connector `.jar` is missing from your classpath.

---

By Khadija and Sultan
