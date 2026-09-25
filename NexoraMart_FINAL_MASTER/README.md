# NexoraMart — Smart Campus Marketplace (Final Master)

NexoraMart is a Java 17 web marketplace for campus-focused shopping, built on Tomcat 9, Maven, H2, HikariCP, JSP/JSTL, Gson and jBCrypt, with JUnit 5/Mockito support.

## Mandatory Capstone Features
- F1 Buyer/Seller registration, bcrypt login, sessions
- F2 Seller product create/edit/delete with rich product specifications and images
- F3 Marketplace browse, search and filtering
- F4 Cart, quantity updates, remove and stock validation
- F5 Transactional checkout, unique order reference, stock deduction and cart clear
- F6 Order history, pending-order cancellation and seller status updates
- F7 Admin moderation, audit logs and analytics
- F8 Buyer reviews/ratings with seller replies

## NexoraMart Differentiators
- Amazon-style storefront and premium responsive UI
- Multi-image product gallery with auto-rotation and zoom
- Visual Search by product photo against the local catalog
- Smart Search with live suggestions and typo-tolerant matching
- Product Compare (up to 3 products)
- Wishlist and Recently Viewed
- Smart Personalization / Picked for You
- Explainable Smart Match with purpose, budget and priority scoring
- Seller Pro command center and inventory signals
- Admin Analytics dashboard
- Notifications center with unread/read state
- Nexora AI Assistant using catalog-aware deterministic recommendations
- Color/theme switching with browser persistence
- PWA manifest + service worker support
- Security headers, custom error pages and validation tests

## Technology / Architecture
The application keeps the capstone's layered architecture:
`Servlet/controller → Service → DAO/JDBC → H2 database`, with JSP/JSTL views and vanilla JavaScript/fetch() for dynamic interactions.

## Build
From the project root:
```powershell
mvn clean verify
```

## Local Deployment
1. Set `JAVA_HOME` to JDK 17.
2. Set `CATALINA_HOME` to the Tomcat 9 installation.
3. Copy `target\nexora-mart.war` to Tomcat `webapps`.
4. Start Tomcat with:
```powershell
& "$env:CATALINA_HOME\bin\catalina.bat" run
```
5. Open:
`http://127.0.0.1:8080/nexora-mart/`

## Test / Demo Order
Register/Login → Marketplace → Smart Search → Visual Search → Product Details → Compare → Wishlist → Add to Cart → Checkout → Orders → Notifications → Smart Match → AI Assistant → Seller Dashboard → Admin Analytics → Theme switch.

## Submission Assets
- `docs/architecture/ARCHITECTURE.md`
- `docs/diagrams/` — ER, Use Case and Sequence diagrams
- `docs/testing/FINAL_TEST_CASES.md`
- `docs/security/SECURITY_CHECKLIST.md`
- `docs/demo/DEMO_SCRIPT.md`
- `.github/workflows/build.yml`

## Repository Hygiene
Do not commit H2 data files, IDE folders, secrets or generated `target/` artifacts. Keep the `main` branch deployable and use conventional commit messages such as `feat:`, `fix:`, `test:` and `docs:`.
