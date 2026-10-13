# Database (MySQL)

- `schema.sql` - the three tables: `users`, `scholarship`, `scholarship_application`
- `sample-data.sql` - sample Indian scholarships

You normally do not need to run these: the Spring Boot backend creates the database `scholarship_db`,
the tables and the sample data automatically on first start. Use them in MySQL Workbench to explain
or inspect the database.

Relationships: one student has many applications; one scholarship has many applications (ManyToOne).

The online (deployed) version uses PostgreSQL on Render with the same three tables, created the same way by Spring Boot.
