CREATE DATABASE car_rental;
USE car_rental;

CREATE TABLE cars (
    car_id VARCHAR(10) PRIMARY KEY,
    brand VARCHAR(100),
    model VARCHAR(100),
    base_price_per_day DOUBLE,
    is_available BOOLEAN
);

CREATE TABLE customers (
    customer_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE rentals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    car_id VARCHAR(10),
    customer_id VARCHAR(20),
    days INT,
    total_price DOUBLE,
    FOREIGN KEY (car_id) REFERENCES cars(car_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
