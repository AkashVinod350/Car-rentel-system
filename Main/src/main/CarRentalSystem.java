/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author snesh
 */
import java.sql.*;
import java.util.Scanner;

public class CarRentalSystem {

    // Insert a car record into the database
    public void addCar(Car car) {
        try (Connection con = Database.getConnection()) {
            String query = "INSERT INTO cars (car_id, brand, model, base_price_per_day, is_available) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, car.getCarId());
            stmt.setString(2, car.getBrand());
            stmt.setString(3, car.getModel());
            stmt.setDouble(4, car.getbasePricePerDay());
            stmt.setBoolean(5, car.isAvailable());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding car: " + e.getMessage());
        }
    }

    // Insert a customer record into the database
    public void addCustomer(Customer customer) {
        try (Connection con = Database.getConnection()) {
            String query = "INSERT INTO customers (customer_id, name) VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, customer.getCustomerId());
            stmt.setString(2, customer.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    // List all available cars from the database
    public void listAvailableCars() {
        try (Connection con = Database.getConnection()) {
            String query = "SELECT * FROM cars WHERE is_available = true";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nAvailable Cars:");
            while (rs.next()) {
                System.out.println(rs.getString("car_id") + " - " + rs.getString("brand") + " " + rs.getString("model"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving available cars: " + e.getMessage());
        }
    }

    // Rent a car: update the car's availability and insert a rental record
    public void rentCar(String carId, Customer customer, int days) {
        try (Connection con = Database.getConnection()) {
            // Check if the car is available
            String selectQuery = "SELECT is_available, base_price_per_day, brand, model FROM cars WHERE car_id = ?";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setString(1, carId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                boolean available = rs.getBoolean("is_available");
                if (available) {
                    double basePrice = rs.getDouble("base_price_per_day");
                    double totalPrice = basePrice * days;
                    
                    // Update car availability to false
                    String updateQuery = "UPDATE cars SET is_available = false WHERE car_id = ?";
                    PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                    updateStmt.setString(1, carId);
                    updateStmt.executeUpdate();
                    
                    // Insert rental record
                    String insertQuery = "INSERT INTO rentals (car_id, customer_id, days, total_price) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                    insertStmt.setString(1, carId);
                    insertStmt.setString(2, customer.getCustomerId());
                    insertStmt.setInt(3, days);
                    insertStmt.setDouble(4, totalPrice);
                    insertStmt.executeUpdate();
                    
                    System.out.println("\n== Rental Information ==");
                    System.out.println("Customer ID: " + customer.getCustomerId());
                    System.out.println("Customer Name: " + customer.getName());
                    System.out.println("Car: " + rs.getString("brand") + " " + rs.getString("model"));
                    System.out.println("Rental Days: " + days);
                    System.out.printf("Total Price: $%.2f%n", totalPrice);
                    System.out.println("\nCar rented successfully.");
                } else {
                    System.out.println("Car is not available for rent.");
                }
            } else {
                System.out.println("Car not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error during rental: " + e.getMessage());
        }
    }

    // Return a car: update availability and remove the rental record
    public void returnCar(String carId) {
        try (Connection con = Database.getConnection()) {
            // Check if there is a rental record for this car
            String selectQuery = "SELECT customer_id FROM rentals WHERE car_id = ?";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setString(1, carId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                String customerId = rs.getString("customer_id");
                // Update car availability to true
                String updateQuery = "UPDATE cars SET is_available = true WHERE car_id = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                updateStmt.setString(1, carId);
                updateStmt.executeUpdate();
                
                // Delete the rental record
                String deleteQuery = "DELETE FROM rentals WHERE car_id = ?";
                PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
                deleteStmt.setString(1, carId);
                deleteStmt.executeUpdate();
                
                // Retrieve customer's name for confirmation
                String customerQuery = "SELECT name FROM customers WHERE customer_id = ?";
                PreparedStatement customerStmt = con.prepareStatement(customerQuery);
                customerStmt.setString(1, customerId);
                ResultSet rsCustomer = customerStmt.executeQuery();
                String customerName = "Unknown";
                if (rsCustomer.next()) {
                    customerName = rsCustomer.getString("name");
                }
                System.out.println("Car returned successfully by " + customerName);
            } else {
                System.out.println("Invalid car ID or car is not rented.");
            }
        } catch (SQLException e) {
            System.out.println("Error during car return: " + e.getMessage());
        }
    }

    // Display a menu and handle user input
    public void menu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Car Rental System =====");
            System.out.println("1. Rent a Car");
            System.out.println("2. Return a Car");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                System.out.println("\n== Rent a Car ==");
                System.out.print("Enter your name: ");
                String customerName = scanner.nextLine();
                // Generate a simple unique customer ID
                String customerId = "CUS" + System.currentTimeMillis();
                Customer customer = new Customer(customerId, customerName);
                addCustomer(customer);

                listAvailableCars();

                System.out.print("\nEnter the car ID you want to rent: ");
                String carId = scanner.nextLine();
                System.out.print("Enter the number of days for rental: ");
                int rentalDays = scanner.nextInt();
                scanner.nextLine(); // consume newline

                rentCar(carId, customer, rentalDays);

            } else if (choice == 2) {
                System.out.println("\n== Return a Car ==");
                System.out.print("Enter the car ID you want to return: ");
                String carId = scanner.nextLine();
                returnCar(carId);

            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid choice. Please enter a valid option.");
            }
        }

        System.out.println("\nThank you for using the Car Rental System!");
        scanner.close();
    }
}
