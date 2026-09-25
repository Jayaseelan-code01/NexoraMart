# Security Checklist

- Passwords stored as bcrypt hashes; plaintext passwords are not logged.
- Protected routes enforce role/session checks.
- Database operations use prepared statements.
- Monetary values use DECIMAL types.
- Session timeout is configured to 30 minutes.
- Browser security headers are applied through a servlet filter.
- Custom 404/500 pages do not expose stack traces.
- User-supplied text is escaped before rendering.
- Database schema and seed data are version controlled.
- CI runs `mvn -B clean verify` on pushes and pull requests.
- Never commit production database credentials or secrets.
