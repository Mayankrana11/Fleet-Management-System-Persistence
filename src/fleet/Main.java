package fleet;

import vehicles.*;
import exceptions.*;
import java.util.*;


//cli driver logic

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final FleetManager manager = new FleetManager();

    public static void main(String[] args) {
        System.out.println("=== Fleet Management System (Assignment 2) ===");

        // Load fleet once from CSV
        manager.loadFromFile("fleet_data.csv");

        while (true) {
            showMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    addVehicleCLI(); //adds vehicle
                    break;
                case "2":
                    removeVehicleCLI(); //removes vehicle
                    break;
                case "3":
                    startJourneyCLI(); //starts journey
                    break;
                case "4":
                    refuelAllCLI();//refuels all vehicles in fleet
                    break;
                case "5":
                    manager.maintainAll();//maintains all vehicles in fleet
                    System.out.println("Maintenance completed.");
                    break;
                case "6":
                    generateAnalyticsReport();//report including avg speed of fleet mlg etc
                    break;
                case "7":
                    manager.displayAllVehicles();//display fleet
                    break;
                case "8":
                    sortAndDisplayMenu();//sorted display via comparators
                    break;
                case "9":
                    showFastestAndSlowest();// speed comparator menu 
                    break;
                case "10":
                    searchByTypeCLI();//seach by type CAR,car,Car,Car,CaR -> results for car
                    break;
                case "11":
                    listMaintenanceNeeds();//lists vehicles which need maintenance like > 10k
                    break;
                case "12":
                    System.out.print("Enter external CSV filename to import: ");//ADD EXTERNAL 
                    String file = sc.nextLine().trim();
                    manager.addFromExternalFile(file);
                    break;
                case "13":
                    System.out.println("Saving fleet before exiting...");//exit
                    manager.saveOnExit();
                    System.out.println("Fleet saved. Exiting program.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }


       //Menu Display

    private static void showMenu() {
        System.out.println(
                "\nFleet Management Menu\n" +
                "1. Add Vehicle\n" +
                "2. Remove Vehicle\n" +
                "3. Start Journey\n" +
                "4. Refuel All\n" +
                "5. Perform Maintenance\n" +
                "6. Generate Performance Report\n" +
                "7. Display All Vehicles\n" +
                "8. Sort & Display Fleet (Model / Speed / Efficiency)\n" +
                "9. Show Fastest & Slowest Vehicles\n" +
                "10. Search by Type\n" +
                "11. List Vehicles Needing Maintenance\n" +
                "12. Add Fleet Data from External CSV\n" +
                "13. Exit\n" +
                "Choose option: "
        );
    }


       //Start Journey (Fixed Output)

    private static void startJourneyCLI() {
        if (manager.getFleetSize() == 0) {
            System.out.println("Fleet is empty. No vehicles to move.");
            return;
        }

        try {
            System.out.print("Enter journey distance (km): ");
            double dist = Double.parseDouble(sc.nextLine().trim());
            int moved = 0;

            for (Vehicle v : manager.getFleetSortedByModel()) {
                System.out.printf("%s (%s): ", v.getId(), v.getModel());
                try {
                    // Let the vehicle print its own internal message once
                    v.move(dist);
                    moved++;
                } catch (Exception e) {
                    // Print the error only once, cleanly indented
                    System.out.printf("Could not complete journey - %s%n", e.getMessage());
                }
            }

            System.out.printf("Journey completed. %d vehicle(s) moved over %.1f km.%n", moved, dist);
        } catch (NumberFormatException e) {
            System.out.println("Invalid distance input.");
        }
    }


// Refuel All Vehicles

    private static void refuelAllCLI() {
        if (manager.getFleetSize() == 0) {
            System.out.println("Fleet is empty.");
            return;
        }

        System.out.print("Enter refuel amount (liters): ");
        try {
            double amount = Double.parseDouble(sc.nextLine().trim());
            int count = 0;
            for (Vehicle v : manager.getFleetSortedByModel()) {
                if (v instanceof interfaces.FuelConsumable) {
                    try {
                        ((interfaces.FuelConsumable) v).refuel(amount);
                        count++;
                    } catch (Exception e) {
                        System.out.println("Refuel failed for " + v.getModel() + ": " + e.getMessage());
                    }
                }
            }
            System.out.println("Refueled " + count + " fuel-based vehicle(s).");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input amount.");
        }
    }


       //Sort & Display Fleet

    private static void sortAndDisplayMenu() {
        System.out.println(
                "Sort by:\n" +
                "1. Model (A–Z)\n" +
                "2. Speed (Fastest First)\n" +
                "3. Efficiency (Most Efficient First)\n"
        );
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();

        List<Vehicle> list = null;
        switch (c) {
            case "1":
                list = manager.getFleetSortedByModel();
                break;
            case "2":
                list = manager.getFleetSortedBySpeed();
                break;
            case "3":
                list = manager.getFleetSortedByEfficiency();
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        if (list == null || list.isEmpty()) {
            System.out.println("No vehicles to display.");
            return;
        }

        for (Vehicle v : list) {
            String powerTag = "";
            if (v instanceof CargoShip) {
                CargoShip ship = (CargoShip) v;
                powerTag = ship.isSailPowered() ? " (SAIL)" : " (FUEL)";
            }

            String effStr;
            if (v instanceof CargoShip && ((CargoShip) v).isSailPowered()) {
                effStr = "∞ (wind-powered)";
            } else if (!(v instanceof interfaces.FuelConsumable)) {
                effStr = "∞ (non-fuel)";
            } else {
                double eff = v.calculateFuelEfficiency();
                effStr = String.format("%.2f km/l", eff);
            }

            System.out.printf("ID: %s, Model: %s%s, Max Speed: %.1f km/h, Efficiency: %s, Mileage: %.1f km%n",
                    v.getId(), v.getModel(), powerTag, v.getMaxSpeed(), effStr, v.getCurrentMileage());
        }
    }


       // Fastest & Slowest

    private static void showFastestAndSlowest() {
        if (manager.getFleetSize() == 0) {
            System.out.println("Fleet is empty.");
            return;
        }

        List<Vehicle> sorted = manager.getFleetSortedBySpeed();
        double maxSpeed = sorted.get(0).getMaxSpeed();
        double minSpeed = sorted.get(sorted.size() - 1).getMaxSpeed();

        System.out.println("Fastest Vehicle(s):");
        for (Vehicle v : sorted) {
            if (v.getMaxSpeed() == maxSpeed) {
                String tag = (v instanceof CargoShip)
                        ? (((CargoShip) v).isSailPowered() ? " (SAIL)" : " (FUEL)")
                        : "";
                System.out.printf("- %s%s (%.1f km/h)%n", v.getModel(), tag, v.getMaxSpeed());
            }
        }

        System.out.println("Slowest Vehicle(s):");
        for (Vehicle v : sorted) {
            if (v.getMaxSpeed() == minSpeed) {
                String tag = (v instanceof CargoShip)
                        ? (((CargoShip) v).isSailPowered() ? " (SAIL)" : " (FUEL)")
                        : "";
                System.out.printf("- %s%s (%.1f km/h)%n", v.getModel(), tag, v.getMaxSpeed());
            }
        }
    }

       //Search Vehicles by Type

    private static void searchByTypeCLI() {
        if (manager.getFleetSize() == 0) {
            System.out.println("Fleet is empty.");
            return;
        }

        System.out.print("Enter type to search (Car/Truck/Bus/Airplane/CargoShip): ");
        String type = sc.nextLine().trim().toLowerCase();
        List<Vehicle> results = new ArrayList<>();

        for (Vehicle v : manager.getFleetSortedByModel()) {
            String className = v.getClass().getSimpleName().toLowerCase();

            boolean match = switch (type) {
                case "car" -> className.equals("car");
                case "truck" -> className.equals("truck");
                case "bus" -> className.equals("bus");
                case "airplane", "plane" -> className.equals("airplane");
                case "cargoship", "cargo ship", "ship" -> className.equals("cargoship");
                default -> false;
            };

            if (match) results.add(v);
        }

        if (results.isEmpty()) {
            System.out.println("No vehicles of type '" + type + "' found.");
        } else {
            System.out.println("\n--- Vehicles of type: " + type.toUpperCase() + " ---");
            for (Vehicle v : results) {
                String tag = "";
                if (v instanceof CargoShip) {
                    tag = ((CargoShip) v).isSailPowered() ? " (SAIL)" : " (FUEL)";
                }
                System.out.printf("ID: %s, Model: %s%s, Max Speed: %.1f km/h, Mileage: %.1f km%n",
                        v.getId(), v.getModel(), tag, v.getMaxSpeed(), v.getCurrentMileage());
            }
        }
    }


       //Maintenance

    private static void listMaintenanceNeeds() {
        List<Vehicle> needs = manager.getVehiclesNeedingMaintenance();
        if (needs.isEmpty()) {
            System.out.println("No vehicles currently require maintenance.");
        } else {
            System.out.println("\n--- Vehicles Needing Maintenance ---");
            for (Vehicle v : needs) v.displayInfo();
        }
    }


       //Performance Report

    private static void generateAnalyticsReport() {
        if (manager.getFleetSize() == 0) {
            System.out.println("Fleet is empty.");
            return;
        }

        System.out.println("\n=== Fleet Performance & Analytics Report ===");

        List<Vehicle> all = manager.getFleetSortedByModel();
        double totalSpeed = 0, totalEff = 0, totalMileage = 0;
        int fuelCount = 0, sailCount = 0, fuelCargoCount = 0;

        Map<String, Integer> typeCount = new LinkedHashMap<>();
        Map<String, Double> typeSpeedSum = new LinkedHashMap<>();

        for (Vehicle v : all) {
            String type = v.getClass().getSimpleName();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            typeSpeedSum.put(type, typeSpeedSum.getOrDefault(type, 0.0) + v.getMaxSpeed());

            totalSpeed += v.getMaxSpeed();
            totalMileage += v.getCurrentMileage();

            if (v instanceof CargoShip ship) {
                if (ship.isSailPowered()) sailCount++;
                else fuelCargoCount++;
            }

            if (v instanceof interfaces.FuelConsumable) {
                totalEff += v.calculateFuelEfficiency();
                fuelCount++;
            }
        }

        double avgSpeed = totalSpeed / all.size();
        double avgEff = (fuelCount > 0) ? (totalEff / fuelCount) : 0;

        System.out.println("\nFleet Overview:");
        System.out.printf("Total Vehicles: %d%n", manager.getFleetSize());
        System.out.printf("Distinct Models: %d%n", manager.getDistinctModels().size());
        System.out.printf("Total Mileage: %.1f km%n", totalMileage);
        System.out.printf("Average Speed (Fleet-wide): %.2f km/h%n", avgSpeed);
        System.out.printf("Average Efficiency (Fuel-based only): %.2f km/l%n", avgEff);

        System.out.println("\nAverage Speed by Vehicle Type:");
        for (Map.Entry<String, Integer> e : typeCount.entrySet()) {
            String type = e.getKey();
            double typeAvg = typeSpeedSum.get(type) / e.getValue();
            System.out.printf("- %-10s: %.2f km/h%n", type, typeAvg);
        }

        System.out.println("\nComposition by Type:");
        for (Map.Entry<String, Integer> e : typeCount.entrySet()) {
            String type = e.getKey();
            if (type.equalsIgnoreCase("CargoShip")) {
                System.out.printf("%-10s: %d (Sail-powered: %d, Fuel-powered: %d)%n",
                        type, e.getValue(), sailCount, fuelCargoCount);
            } else {
                System.out.printf("%-10s: %d%n", type, e.getValue());
            }
        }

        List<Vehicle> sorted = manager.getFleetSortedBySpeed();
        double maxSpeed = sorted.get(0).getMaxSpeed();
        double minSpeed = sorted.get(sorted.size() - 1).getMaxSpeed();

        System.out.println("\nPerformance Highlights:");
        System.out.println("Fastest Vehicle(s):");
        for (Vehicle v : sorted) {
            if (v.getMaxSpeed() == maxSpeed) {
                String tag = (v instanceof CargoShip)
                        ? (((CargoShip) v).isSailPowered() ? " (SAIL)" : " (FUEL)")
                        : "";
                System.out.printf("- %s%s (%.1f km/h)%n", v.getModel(), tag, v.getMaxSpeed());
            }
        }

        System.out.println("Slowest Vehicle(s):");
        for (Vehicle v : sorted) {
            if (v.getMaxSpeed() == minSpeed) {
                String tag = (v instanceof CargoShip)
                        ? (((CargoShip) v).isSailPowered() ? " (SAIL)" : " (FUEL)")
                        : "";
                System.out.printf("- %s%s (%.1f km/h)%n", v.getModel(), tag, v.getMaxSpeed());
            }
        }
    }


//Add / Remove

    private static void addVehicleCLI() {
        try {
            System.out.print("Enter type (Car/Truck/Bus/Airplane/CargoShip): ");
            String type = sc.nextLine().trim();
            System.out.print("Enter ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Enter model: ");
            String model = sc.nextLine().trim();
            System.out.print("Enter max speed (km/h): ");
            double maxSpeed = Double.parseDouble(sc.nextLine().trim());

            Vehicle v = createVehicleFromInput(type, id, model, maxSpeed);
            if (v == null) {
                System.out.println("Unknown vehicle type.");
                return;
            }

            manager.addVehicle(v);
            System.out.println("Vehicle added successfully.");
        } catch (InvalidOperationException e) {
            System.err.println("Add failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void removeVehicleCLI() {
        System.out.print("Enter ID to remove: ");
        String id = sc.nextLine().trim();
        try {
            manager.removeVehicle(id);
            System.out.println("Vehicle removed successfully.");
        } catch (InvalidOperationException e) {
            System.err.println("Remove failed: " + e.getMessage());
        }
    }


       //Vehicle Creation

    private static Vehicle createVehicleFromInput(String type, String id, String model, double maxSpeed) {
        String t = (type == null) ? "" : type.trim().toLowerCase();
        return switch (t) {
            case "car" -> new Car(id, model, maxSpeed, 4);
            case "truck" -> new Truck(id, model, maxSpeed, 6);
            case "bus" -> new Bus(id, model, maxSpeed, 6);
            case "airplane", "plane" -> {
                System.out.print("Enter max altitude (in meters): ");
                double altitude = Double.parseDouble(sc.nextLine().trim());
                yield new Airplane(id, model, maxSpeed, altitude);
            }
            case "cargoship", "cargo ship", "ship" -> {
                System.out.print("Is this ship sail-powered? (true/false): ");
                boolean hasSail = Boolean.parseBoolean(sc.nextLine().trim());
                yield new CargoShip(id, model, maxSpeed, hasSail);
            }
            default -> null;
        };
    }
}
