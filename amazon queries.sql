create database ai_ecommerce;
select * from products;
alter table products 
add column image_url varchar(500);

update products
set image_url = "http://localhost:8080/images/KREO_HIVE_65.webp"
where id=10;

create table users (
id bigint auto_increment primary key,
name varchar(100),
email varchar(100) unique,
password varchar(200),
role varchar(100)
);
ALTER TABLE users ADD COLUMN phone_number VARCHAR(15);
ALTER TABLE users DROP COLUMN phone;
ALTER TABLE users DROP COLUMN phone_nu;

select * from users;

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    total_amount DOUBLE,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    price DOUBLE
);