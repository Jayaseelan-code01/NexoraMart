# NexoraMart Final Regression Test Cases

| ID | Area | Test | Expected |
|---|---|---|---|
| TC-01 | Auth | Register as Buyer | Account created; correct role stored; user can log in |
| TC-02 | Auth | Login with wrong password | Login rejected without exposing password details |
| TC-03 | Marketplace | Open marketplace | Products render with image, price, stock and specifications |
| TC-04 | Search | Search by keyword / typo | Relevant results and live suggestions appear |
| TC-05 | Visual Search | Upload a catalog product photo | Closest catalog matches show image and product details |
| TC-06 | Details | Open product details | Gallery, specs, price, stock, reviews and actions render |
| TC-07 | Cart | Add, update, remove item | Cart totals recalculate correctly |
| TC-08 | Cart | Set quantity above stock | Request rejected with stock message |
| TC-09 | Checkout | Place an order | Unique order reference created; cart cleared; stock reduced |
| TC-10 | Orders | View order history | Current user's orders only are displayed |
| TC-11 | Orders | Cancel pending order | Order becomes CANCELLED and eligible stock is restored |
| TC-12 | Seller | Add/edit/delete own product | Seller can manage only owned catalog items |
| TC-13 | Seller | Update order status | Only allowed status transitions accepted |
| TC-14 | Reviews | Submit 1–5 star review | Review stored and shown on product |
| TC-15 | Reviews | Submit second review for same product | Duplicate review blocked |
| TC-16 | Wishlist | Add/remove wishlist item | Wishlist state updates without affecting cart |
| TC-17 | Compare | Compare up to 3 items | Side-by-side specs render; add-to-cart remains usable |
| TC-18 | Notifications | Open/read notification | Unread count updates; notification can be opened |
| TC-19 | Smart Match | Set purpose + budget + priority | Explainable ranked matches returned |
| TC-20 | AI Assistant | Ask product query | Catalog-aware result with relevant product links |
| TC-21 | Admin | Moderate a user/product/order | Admin-only controls execute and are audited |
| TC-22 | Analytics | Open admin analytics | Current database metrics render without hard-coded demo counts |
| TC-23 | Security | Send SQL/XSS payload | Input is rejected/escaped; no query concatenation is introduced |
| TC-24 | Error pages | Open unknown route / trigger server error | Custom 404/500 page shown without stack trace |
