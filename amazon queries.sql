create database ai_ecommerce;
select * from products;
alter table products 
add column image_url varchar(500);

update products
set image_url = "http://localhost:8080/images/KREO_HIVE_65.webp"
where id=10;