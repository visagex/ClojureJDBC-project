# What is it?
ClojureJDBC‑Project is a small Clojure-based command-line tool for managing the database of an event management system via JDBC. It lets users initialize, drop, add, update, and delete database tables and records via simple commands.

# What problem(s) does it strive to solve?
Managing the schema and data of an event management system often involves writing SQL, running migrations, or wiring up data access layers. This project aims to simplify that by:
Allowing users to create (init) or drop tables easily via commands, without manually writing DDL.
Providing data-level operations (insert, update, delete) against those tables via a uniform interface.
Abstracting away JDBC boilerplate so that the user can focus on domain logic (i.e. events, attendance) instead of raw SQL/JDBC.
Serving as a learning tool / project scaffold for database‑driven Clojure applications.
# Core abstractions
The main abstractions are:
Database / JDBC abstraction — a wrapper around JDBC to run SQL, manage connections, execute DDL or DML.
Table / Schema abstractions — representing tables (e.g. events, attendance) with columns, definitions, etc.
Command / CLI abstractions — mapping user commands (e.g. init, drop-table, add, update, delete) to operations on the schema or data.
Entities / Domain tables — the domain model (events, participants, attendance, etc) which map to underlying database tables.
These abstractions separate what operations (commands) from how they are executed (JDBC / SQL).
# Primary operations
Here are the key operations (commands) the tool supports:
```init``` / “initialize tables”	Creates all necessary tables for the event management schema in the database
```drop-table``` <tablename>	Drops the specified table
```add```	Insert a record into a given table
```update```	Update an existing record in a table
```delete```	Delete a record from a table
```help```, help events, help attendance, etc	Print help / usage instructions for commands or domain areas
These operations allow the user to fully manage both schema and data at runtime via a set of commands.
# Architectural Components
CLI / Command Dispatcher: The entry point reads the user’s command arguments, parses which operation is requested, and dispatches to the correct handler.
Schema / DDL Manager: Provides functions to generate and run DDL statements to create or drop tables.
Data Access / JDBC Layer: Wraps database connection setup, statement execution, transaction handling, and error handling.
Domain Layer: Contains domain-specific logic (e.g. event, attendance) that knows table names, column names, and how to map user-provided values to SQL statements.
Configuration / Dependency Definitions: deps.edn for dependencies (incl. JDBC driver, etc), configuration for connecting to the database (host, port, user, password).
There are no external services (e.g. web servers, message queues) — it’s a single binary/CLI tool that interacts directly with a relational DB via JDBC.
Dependencies include a [next.jdbc]([url](https://github.com/seancorfield/next-jdbc)) and [HoneySql]([url](https://github.com/seancorfield/honeysql)).
# Is it simple?
In many respects, yes:
It is stateless (except for the DB) — each command runs, executes SQL, and exits.
No circular dependencies — the layers are fairly linear (CLI → domain → JDBC).
There is no heavy infrastructure or external components; everything is local.
# Fundamental tradeoffs
Generates SQL in code, but avoids the complexity of full-blown ORM frameworks.
No built-in migration system: schema evolution beyond “init / drop” would require manual code changes.
No abstraction over different RDBMS dialects: It assumes a single SQL dialect (MySql) which may reduce portability.
Error propagation / rollback sophistication is limited: if operations fail midway, you trade off ease-of-implementation for more manual control of transaction boundaries.
CLI-only interface: It’s not built as a service or web API, so integration with other systems is limited unless extended.

# How to run: 
Prerequisites
Clojure CLI tools
Java 8 or higher
A running MySQL database
Internet access to download dependencies (first time)
Running the Project
Use the Clojure CLI to run commands from the terminal:
```clojure -M -m core <command> [arguments]```
Replace <command> and [arguments] with what you want to do.
# Example Commands
# Create all required tables in the database
```clojure -M -m core init```

# Drop a specific table
```clojure -M -m core drop-table events```

# Add a new event
```clojure -M -m core add event "Event Name" "2025-10-07" "Location"```

# Update an event (example format may vary)
```clojure -M -m core update event 1 "New Name" "2025-12-01" "New Location"```

# Delete an event by ID
```clojure -M -m core delete event 1```

# Show help
```clojure -M -m core help```

Configure the Database (MySQL)
Edit the database settings in src/db.clj:
```
(def db-spec
  {:classname   "com.mysql.cj.jdbc.Driver"
   :subprotocol "mysql"
   :subname     "//localhost:3306/your_database_name"
   :user        "your_mysql_username"
   :password    "your_mysql_password"})
```
Make sure you have the MySQL JDBC driver in your dependencies (check deps.edn) and that the MySQL server is running and accessible.
