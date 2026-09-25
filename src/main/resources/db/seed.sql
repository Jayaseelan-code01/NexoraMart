INSERT INTO users(name,email,password_hash,role,active)
SELECT 'Nexora Admin','admin@nexora.local','$2a$10$vHBVWfoP/jyxq5XLhQjjje3KiXyIIaePHKJrOQZ0yTg9nyDtt/bJu','ADMIN',TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='admin@nexora.local');

INSERT INTO users(name,email,password_hash,role)
SELECT 'Nexora Demo Seller','demo.seller@nexora.local','DEMO','SELLER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='demo.seller@nexora.local');

INSERT INTO products(name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT 'Nexora Phone X1','Nexora','X1','Campus-ready 5G smartphone with AMOLED display and all-day battery.',29999,8,'Mobile','8 GB','256 GB','6.7-inch AMOLED','50 MP + 8 MP','5000 mAh','images/nexora-x1-1.svg','images/nexora-x1-2.svg','images/nexora-x1-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Phone X1');
INSERT INTO products(name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT 'Nexora Phone Pro','Nexora','Pro','Performance-focused phone with 120Hz display and large storage.',39999,6,'Mobile','12 GB','256 GB','6.8-inch AMOLED 120Hz','50 MP OIS + 12 MP','5100 mAh','images/nexora-pro-1.svg','images/nexora-pro-2.svg','images/nexora-pro-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Phone Pro');
INSERT INTO products(name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT 'Nexora Phone Lite','Nexora','Lite','Lightweight student phone with balanced performance and battery life.',21999,12,'Mobile','6 GB','128 GB','6.5-inch AMOLED','50 MP + 2 MP','5000 mAh','images/nexora-lite-1.svg','images/nexora-lite-2.svg','images/nexora-lite-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Phone Lite');
INSERT INTO products(name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT 'Nexora Phone Max','Nexora','Max','Big-screen phone designed for student productivity and entertainment.',32999,7,'Mobile','8 GB','256 GB','6.9-inch AMOLED','108 MP + 8 MP','6000 mAh','images/nexora-max-1.svg','images/nexora-max-2.svg','images/nexora-max-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Phone Max');
INSERT INTO products(name,brand,model,description,price,stock_qty,category,image_url_1,image_url_2,image_url_3)
SELECT 'Nexora Wireless Buds','Nexora','Buds S','Compact wireless earbuds for classes, travel and calls.',2499,20,'Audio','images/buds.svg','images/buds.svg','images/buds.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Wireless Buds');
INSERT INTO products(name,brand,model,description,price,stock_qty,category,image_url_1,image_url_2,image_url_3)
SELECT 'Nexora Study Backpack','Nexora','Campus 25L','Water-resistant backpack with organized student pockets.',1999,15,'Accessories','images/backpack.svg','images/backpack.svg','images/backpack.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Study Backpack');


-- Ultimate edition: link seeded catalog products to the demo seller so seller-side
-- moderation/reply screens have real catalog ownership.
UPDATE products SET seller_id=(SELECT id FROM users WHERE email='demo.seller@nexora.local')
WHERE seller_id IS NULL;

UPDATE products SET image_url_1='images/nexora-buds-1.svg', image_url_2='images/nexora-buds-2.svg', image_url_3='images/nexora-buds-3.svg' WHERE name='Nexora Wireless Buds';
UPDATE products SET image_url_1='images/nexora-backpack-1.svg', image_url_2='images/nexora-backpack-2.svg', image_url_3='images/nexora-backpack-3.svg' WHERE name='Nexora Study Backpack';

INSERT INTO products(seller_id,name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT (SELECT id FROM users WHERE email='demo.seller@nexora.local'),'Nexora PixelPad 11','Nexora','PixelPad 11','11-inch 120Hz study tablet with split-screen notes and media.',28999,9,'Tablet','8 GB','256 GB','11-inch 120Hz','13 MP','8000 mAh','images/nexora-pixelpad-1.svg','images/nexora-pixelpad-2.svg','images/nexora-pixelpad-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora PixelPad 11');

INSERT INTO products(seller_id,name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT (SELECT id FROM users WHERE email='demo.seller@nexora.local'),'Nexora Book Air','Nexora','Book Air','Slim 14-inch laptop for coding, presentations and everyday campus work.',57999,5,'Laptop','16 GB','512 GB','14-inch FHD','1080p webcam','56 Wh','images/nexora-book-1.svg','images/nexora-book-2.svg','images/nexora-book-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Book Air');

INSERT INTO products(seller_id,name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT (SELECT id FROM users WHERE email='demo.seller@nexora.local'),'Nexora StudentBook','Nexora','StudentBook','Balanced student notebook with a large display and upgrade-friendly storage.',42999,8,'Laptop','8 GB','512 GB','15.6-inch FHD','1080p webcam','52 Wh','images/nexora-student-1.svg','images/nexora-student-2.svg','images/nexora-student-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora StudentBook');

INSERT INTO products(seller_id,name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT (SELECT id FROM users WHERE email='demo.seller@nexora.local'),'Nexora Watch Fit','Nexora','Watch Fit','AMOLED wearable with health tracking, timers and class-day notifications.',6999,14,'Wearable','4 GB','32 GB','1.8-inch AMOLED','12 MP','480 mAh','images/nexora-watch-1.svg','images/nexora-watch-2.svg','images/nexora-watch-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Watch Fit');

INSERT INTO products(seller_id,name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT (SELECT id FROM users WHERE email='demo.seller@nexora.local'),'Nexora Power 20K','Nexora','Power 20K','High-capacity power bank for long campus days and travel.',1799,25,'Power','-','20,000 mAh','-','-','20000 mAh','images/nexora-power-1.svg','images/nexora-power-2.svg','images/nexora-power-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora Power 20K');

INSERT INTO products(seller_id,name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT (SELECT id FROM users WHERE email='demo.seller@nexora.local'),'Nexora MechKeys K1','Nexora','K1','Compact mechanical keyboard for coding labs, gaming and study setups.',3299,18,'Accessories','-','-','87-key RGB','-','-','images/nexora-keyboard-1.svg','images/nexora-keyboard-2.svg','images/nexora-keyboard-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora MechKeys K1');

INSERT INTO products(seller_id,name,brand,model,description,price,stock_qty,category,ram,storage,display_size,camera,battery,image_url_1,image_url_2,image_url_3)
SELECT (SELECT id FROM users WHERE email='demo.seller@nexora.local'),'Nexora USB-C Hub 7','Nexora','Hub 7','Seven-port USB-C hub for laptops, presentations and project setups.',2499,22,'Accessories','-','-','7-in-1','-','-','images/nexora-hub-1.svg','images/nexora-hub-2.svg','images/nexora-hub-3.svg'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name='Nexora USB-C Hub 7');
