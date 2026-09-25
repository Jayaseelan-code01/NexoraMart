package com.jayaseelan.nexoramart.controller;

import com.google.gson.Gson;
import com.jayaseelan.nexoramart.dao.JdbcProductDAO;
import com.jayaseelan.nexoramart.model.Product;
import com.jayaseelan.nexoramart.service.ProductService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/assistant")
public class AssistantServlet extends HttpServlet {
    private static final Pattern MONEY = Pattern.compile("(?:under|below|less than|max|budget|within)\\s*(?:₹|rs\\.?|inr)?\\s*([0-9][0-9,]*)", Pattern.CASE_INSENSITIVE);
    private ProductService service;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        service = new ProductService(new JdbcProductDAO(
                (DataSource) getServletContext().getAttribute("dataSource")));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> out = new LinkedHashMap<>();
        String raw = Optional.ofNullable(request.getParameter("message")).orElse("").trim();
        String message = raw.toLowerCase(Locale.ROOT);

        try {
            if (message.isBlank()) {
                out.put("reply", "Tell me what you need — for example: ‘gaming phone under 30000’, ‘best battery phone’, ‘coding laptop’, ‘compare’, or ‘show my orders’.");
                out.put("suggestions", quickSuggestions());
            } else if (isGreeting(message)) {
                out.put("reply", "Hi! I’m Nexora Assistant. I can search the campus catalog, explain product matches, help with budget choices, and take you to Cart, Orders or Compare.");
                out.put("suggestions", quickSuggestions());
            } else if (message.contains("cart")) {
                out.put("reply", "Your cart is ready. You can update quantity, remove products, or continue to checkout.");
                out.put("link", request.getContextPath() + "/cart");
            } else if (message.contains("order") || message.contains("delivery") || message.contains("track")) {
                out.put("reply", "Open My Orders to check your order reference, status and eligible cancellation options.");
                out.put("link", request.getContextPath() + "/orders");
            } else if (message.contains("compare")) {
                out.put("reply", "Choose up to three products in the marketplace and open Compare for a side-by-side specification view.");
                out.put("link", request.getContextPath() + "/marketplace#catalog");
            } else {
                List<Product> candidates = service.browse(null, null);
                ParsedIntent intent = parseIntent(message);
                List<Scored> scored = new ArrayList<>();

                for (Product p : candidates) {
                    int score = 0;
                    List<String> reasons = new ArrayList<>();
                    String hay = normalize(String.join(" ",
                            nz(p.getName()), nz(p.getBrand()), nz(p.getModel()), nz(p.getCategory()),
                            nz(p.getDescription()), nz(p.getRam()), nz(p.getStorage()),
                            nz(p.getDisplaySize()), nz(p.getCamera()), nz(p.getBattery())));

                    if (p.getStockQty() <= 0) continue;
                    score += 4;

                    if (!intent.category.isBlank() && hay.contains(intent.category)) {
                        score += 26;
                        reasons.add("matches your category");
                    }
                    for (String token : intent.keywords) {
                        if (token.length() >= 3 && hay.contains(token)) {
                            score += 5;
                        }
                    }

                    if (intent.budget != null && p.getPrice() != null) {
                        if (p.getPrice().compareTo(intent.budget) <= 0) {
                            score += 24;
                            reasons.add("within your budget");
                        } else {
                            score -= 18;
                        }
                    }

                    if (intent.purpose.equals("gaming")) {
                        int ram = number(p.getRam());
                        int storage = number(p.getStorage());
                        if (ram >= 12) { score += 14; reasons.add("higher RAM for performance"); }
                        else if (ram >= 8) score += 8;
                        if (storage >= 256) { score += 8; reasons.add("ample storage"); }
                    }
                    if (intent.purpose.equals("coding")) {
                        int ram = number(p.getRam());
                        if (ram >= 16) { score += 18; reasons.add("16 GB-class memory"); }
                        else if (ram >= 8) score += 9;
                        if (hay.contains("laptop") || hay.contains("tablet")) { score += 12; reasons.add("suited to coding work"); }
                    }
                    if (intent.purpose.equals("study")) {
                        if (hay.contains("student") || hay.contains("study") || hay.contains("campus")) { score += 15; reasons.add("campus-friendly profile"); }
                        if (p.getStockQty() >= 5) score += 3;
                    }
                    if (intent.purpose.equals("camera")) {
                        int cam = number(p.getCamera());
                        if (cam >= 100) { score += 18; reasons.add("camera-focused spec"); }
                        else if (cam >= 50) score += 10;
                    }
                    if (intent.purpose.equals("battery")) {
                        int battery = number(p.getBattery());
                        if (battery >= 6000) { score += 20; reasons.add("large battery"); }
                        else if (battery >= 5000) score += 11;
                    }
                    if (intent.priority.equals("value") && p.getPrice() != null) {
                        if (p.getPrice().compareTo(new BigDecimal("10000")) <= 10000) score += 4;
                    }

                    if (!reasons.isEmpty()) score += Math.min(10, reasons.size() * 2);
                    if (score > 4) scored.add(new Scored(p, score, reasons));
                }

                scored.sort((a, b) -> Integer.compare(b.score, a.score));
                List<Map<String, Object>> picks = new ArrayList<>();
                for (int i = 0; i < Math.min(4, scored.size()); i++) {
                    Product p = scored.get(i).product;
                    Scored s = scored.get(i);
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", p.getId());
                    m.put("name", p.getName());
                    m.put("brand", p.getBrand());
                    m.put("model", p.getModel());
                    m.put("price", p.getPrice());
                    m.put("category", p.getCategory());
                    m.put("ram", p.getRam());
                    m.put("storage", p.getStorage());
                    m.put("display", p.getDisplaySize());
                    m.put("camera", p.getCamera());
                    m.put("battery", p.getBattery());
                    m.put("stock", p.getStockQty());
                    m.put("image", request.getContextPath() + "/" + nz(p.getImageUrl1()));
                    m.put("score", Math.min(99, s.score * 2));
                    m.put("reason", s.reason());
                    picks.add(m);
                }

                if (picks.isEmpty()) {
                    out.put("reply", "I could not find a strong catalog match. Try a category or budget, such as ‘phone under 30000’, ‘coding laptop’, or ‘long battery’.");
                    out.put("suggestions", Arrays.asList("phone under 30000", "coding laptop", "long battery phone", "gaming phone"));
                } else {
                    out.put("reply", buildReply(intent, picks.size()));
                    out.put("products", picks);
                }
            }
        } catch (Exception e) {
            getServletContext().log("Assistant failed", e);
            out.clear();
            out.put("reply", "The assistant is temporarily unavailable. You can still use Search, Smart Match, Compare and Categories.");
            out.put("suggestions", quickSuggestions());
        }

        response.getWriter().print(gson.toJson(out));
    }

    private String buildReply(ParsedIntent intent, int count) {
        StringBuilder b = new StringBuilder("I found ").append(count).append(" catalog match").append(count == 1 ? "" : "es").append(".");
        if (!intent.category.isBlank()) b.append(" I focused on ").append(intent.category).append(" products.");
        if (intent.budget != null) b.append(" I kept the budget at ₹").append(intent.budget.toPlainString()).append(" where possible.");
        b.append(" Each card shows the reason it matched.");
        return b.toString();
    }

    private ParsedIntent parseIntent(String message) {
        ParsedIntent i = new ParsedIntent();
        i.category = detectCategory(message);
        i.purpose = detectPurpose(message);
        i.priority = detectPriority(message);
        i.budget = parseBudget(message);
        String clean = normalize(message);
        i.keywords = new ArrayList<>();
        for (String token : clean.split("\\s+")) {
            if (token.length() >= 3 && !STOP.contains(token)) i.keywords.add(token);
        }
        return i;
    }

    private String detectCategory(String m) {
        if (containsAny(m, "phone", "mobile", "smartphone", "iphone")) return "mobile";
        if (containsAny(m, "laptop", "notebook", "computer", "pc")) return "laptop";
        if (containsAny(m, "tablet", "ipad")) return "tablet";
        if (containsAny(m, "earbud", "buds", "headphone", "audio")) return "audio";
        if (containsAny(m, "bag", "backpack")) return "accessories";
        if (containsAny(m, "watch", "wearable")) return "wearable";
        if (containsAny(m, "powerbank", "power bank", "charger")) return "power";
        if (containsAny(m, "keyboard", "hub", "accessory")) return "accessories";
        return "";
    }

    private String detectPurpose(String m) {
        if (containsAny(m, "gaming", "game", "gamer")) return "gaming";
        if (containsAny(m, "coding", "programming", "developer", "development")) return "coding";
        if (containsAny(m, "study", "student", "college", "class", "campus")) return "study";
        if (containsAny(m, "camera", "photography", "photo")) return "camera";
        if (containsAny(m, "battery", "long battery", "all day")) return "battery";
        return "";
    }

    private String detectPriority(String m) {
        if (containsAny(m, "cheapest", "cheap", "value", "budget")) return "value";
        if (containsAny(m, "performance", "fast", "powerful")) return "performance";
        if (containsAny(m, "camera")) return "camera";
        if (containsAny(m, "battery")) return "battery";
        return "balanced";
    }

    private BigDecimal parseBudget(String message) {
        Matcher matcher = MONEY.matcher(message);
        if (!matcher.find()) return null;
        try { return new BigDecimal(matcher.group(1).replace(",", "")); }
        catch (NumberFormatException ignored) { return null; }
    }

    private boolean isGreeting(String m) { return containsAny(m, "hello", "hi", "hey", "vanakkam"); }
    private boolean containsAny(String s, String... values) { for (String v : values) if (s.contains(v)) return true; return false; }
    private String normalize(String s) { return s == null ? "" : s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim(); }
    private String nz(String s) { return s == null ? "" : s; }
    private int number(String s) {
        if (s == null) return 0;
        Matcher m = Pattern.compile("(\\d+(?:[.,]\\d+)?)").matcher(s.replace(",", ""));
        if (!m.find()) return 0;
        try { return (int) Double.parseDouble(m.group(1)); } catch (NumberFormatException e) { return 0; }
    }
    private List<String> quickSuggestions() {
        return Arrays.asList("best phone under 30000", "gaming phone", "coding laptop", "long battery phone");
    }

    private static final Set<String> STOP = new HashSet<>(Arrays.asList(
            "the", "and", "for", "with", "from", "this", "that", "need", "want", "show", "give", "best", "under", "below", "phone", "mobile"
    ));

    private static class ParsedIntent {
        String category = "", purpose = "", priority = "";
        BigDecimal budget;
        List<String> keywords = Collections.emptyList();
    }

    private static class Scored {
        final Product product;
        final int score;
        final List<String> reasons;
        Scored(Product product, int score, List<String> reasons) {
            this.product = product; this.score = score; this.reasons = reasons;
        }
        String reason() {
            if (reasons.isEmpty()) return "catalog keyword match";
            return String.join(" + ", reasons);
        }
    }
}
