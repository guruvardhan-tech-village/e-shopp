# 🐘 PostgreSQL Commands Guide (Basic → Advanced)

## 📌 Introduction

PostgreSQL is a powerful open-source relational database system used in production-level applications. It supports advanced querying, strong data integrity, and scalability.

---

# 🟢 1. BASIC COMMANDS

## 🔹 Connect to PostgreSQL

```bash
psql -U postgres
```

👉 Connects to PostgreSQL using user `postgres`

---

## 🔹 Create Database

```sql
CREATE DATABASE mydb;
```

👉 Creates a new database named `mydb`

---

## 🔹 List Databases

```sql
SELECT datname FROM pg_database;
```

👉 Shows all databases (PostgreSQL alternative to `SHOW DATABASES`)

---

## 🔹 Connect to Database

```sql
\c mydb
```

👉 Switch to database `mydb`

---

## 🔹 Delete Database

```sql
DROP DATABASE mydb;
```

👉 Deletes database permanently ⚠️

---

# 🟡 2. TABLE OPERATIONS

## 🔹 Create Table

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(100),
    age INT
);
```

👉 Creates table with auto-increment ID

---

## 🔹 View Tables

```sql
\dt
```

---

## 🔹 Describe Table

```sql
\d users
```

---

## 🔹 Drop Table

```sql
DROP TABLE users;
```

---

# 🔵 3. CRUD OPERATIONS

## 🔹 Insert Data

```sql
INSERT INTO users (name, email, age)
VALUES ('Jack', 'jack@gmail.com', 22);
```

---

## 🔹 Select Data

```sql
SELECT * FROM users;
```

---

## 🔹 Update Data

```sql
UPDATE users
SET age = 23
WHERE id = 1;
```

---

## 🔹 Delete Data

```sql
DELETE FROM users WHERE id = 1;
```

---

# 🟣 4. CONDITIONS & FILTERING

## 🔹 WHERE Clause

```sql
SELECT * FROM users WHERE age > 20;
```

---

## 🔹 AND / OR

```sql
SELECT * FROM users WHERE age > 20 AND name = 'Jack';
```

---

## 🔹 LIKE (Pattern Matching)

```sql
SELECT * FROM users WHERE name LIKE 'J%';
```

---

## 🔹 ORDER BY

```sql
SELECT * FROM users ORDER BY age DESC;
```

---

## 🔹 LIMIT

```sql
SELECT * FROM users LIMIT 5;
```

---

# 🟠 5. AGGREGATE FUNCTIONS

## 🔹 Count

```sql
SELECT COUNT(*) FROM users;
```

## 🔹 Average

```sql
SELECT AVG(age) FROM users;
```

## 🔹 Sum

```sql
SELECT SUM(age) FROM users;
```

## 🔹 Group By

```sql
SELECT age, COUNT(*)
FROM users
GROUP BY age;
```

---

# 🔴 6. JOINS (IMPORTANT 🔥)

## 🔹 Inner Join

```sql
SELECT u.name, o.amount
FROM users u
JOIN orders o ON u.id = o.user_id;
```

👉 Returns matching records from both tables

---

## 🔹 Left Join

```sql
SELECT u.name, o.amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id;
```

👉 Returns all users + matching orders

---

# 🟤 7. CONSTRAINTS

## 🔹 NOT NULL

```sql
name VARCHAR(50) NOT NULL
```

## 🔹 UNIQUE

```sql
email VARCHAR(100) UNIQUE
```

## 🔹 DEFAULT

```sql
age INT DEFAULT 18
```

---

# ⚫ 8. INDEXES (Performance 🚀)

## 🔹 Create Index

```sql
CREATE INDEX idx_email ON users(email);
```

👉 Speeds up search queries

---

# ⚡ 9. ADVANCED QUERIES

## 🔹 Subquery

```sql
SELECT * FROM users
WHERE age > (SELECT AVG(age) FROM users);
```

---

## 🔹 CASE Statement

```sql
SELECT name,
CASE 
    WHEN age > 18 THEN 'Adult'
    ELSE 'Minor'
END
FROM users;
```

---

## 🔹 Transactions

```sql
BEGIN;

UPDATE users SET age = 25 WHERE id = 1;

ROLLBACK; -- Undo
-- OR
COMMIT; -- Save
```

---

# 🔐 10. USER & PERMISSIONS

## 🔹 Create User

```sql
CREATE USER myuser WITH PASSWORD '1234';
```

## 🔹 Grant Access

```sql
GRANT ALL PRIVILEGES ON DATABASE mydb TO myuser;
```

---

# 🧠 11. INTERVIEW POINTS

* PostgreSQL is **ACID compliant**
* Uses **MVCC (Multi-Version Concurrency Control)**
* Supports **JSON + relational data**
* Preferred for **production systems**

---

# 🚀 Conclusion

PostgreSQL is a powerful database used in:

* Banking systems
* Web applications
* Enterprise software

👉 Mastering these commands = strong backend foundation

---

# 💡 Bonus Tip

Use pgAdmin GUI for:

* Easier table creation
* Query execution
* Visual database management

---
