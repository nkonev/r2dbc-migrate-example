INSERT INTO customer (first_name, last_name)
VALUES
(E'Name with \$', 'Escape example 1'),
($$Pokemon for 5 $.$$, 'See more examples at https://www.postgresql.org/docs/12/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS');