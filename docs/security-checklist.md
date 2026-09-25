# NexoraMart Security Checklist

- [x] Passwords hashed with bcrypt; never stored as plaintext.
- [x] Monetary values remain database DECIMAL fields.
- [x] Browser security headers added through SecurityHeadersFilter.
- [x] Custom 404 and 500 pages avoid stack-trace disclosure.
- [x] Sensitive local database files excluded by .gitignore.
- [x] Automated unit tests cover password hashing and validation utilities.
- [ ] Manually verify every protected route redirects unauthenticated users.
- [ ] Manually verify SQL-injection and XSS payload handling before submission.
