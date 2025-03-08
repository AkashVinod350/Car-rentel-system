package main;
public class Main {
    public static void main(String[] args) {
        // Ensure your MySQL database is running and the tables are created.
        // Run the SQL script provided above before starting this program.

        CarRentalSystem rentalSystem = new CarRentalSystem();

        // Add some cars to the database.
        // If these records already exist, you may want to skip adding duplicates.
//        rentalSystem.addCar(new Car("C001", "Toyota", "Camry", 60.0));
//        rentalSystem.addCar(new Car("C002", "Honda", "Accord", 70.0));
//        rentalSystem.addCar(new Car("C003", "Mahindra", "Thar", 150.0));

        rentalSystem.menu();
    }
}
