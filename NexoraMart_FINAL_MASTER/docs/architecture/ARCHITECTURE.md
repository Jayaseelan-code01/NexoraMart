# NexoraMart Architecture

```text
Browser / JSP + Vanilla JS + fetch()
                |
             Servlets
                |
             Services
                |
          DAO interfaces
                |
       JDBC DAO implementations
                |
          HikariCP DataSource
                |
              H2 DB
```

Supporting layers include model/DTO classes, security and encoding filters, password/input utilities, servlet context database initialization, custom error pages and structured logging.

## Required diagrams

- `diagrams/ER_Diagram.png` — entities and relationships derived from `schema.sql`.
- `diagrams/UseCase_Diagram.png` — Buyer, Seller and Admin use cases for F1–F8.
- `diagrams/Sequence_Diagram.png` — place-order request/response path.
