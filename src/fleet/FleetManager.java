package fleet;

import vehicles.*;
import exceptions.*;
import interfaces.Maintainable;
import util.SortByModel;
import util.SortBySpeed;
import util.SortByEfficiency;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FleetManager {

    private final List<Vehicle> fleet = new ArrayList<>();
    private final Set<String> distinctModels = new HashSet<>();

    public FleetManager() {
        File file = new File("fleet_data.csv");
        if (!file.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("Type,ID,Model,MaxSpeed,Efficiency,CurrentMileage,HasSail");
            } catch (IOException ignored) {}
        }
    }

    public void addVehicle(Vehicle v) throws InvalidOperationException {
        if (v == null) throw new InvalidOperationException("Null vehicle cannot be added");
        if (fleet.stream().anyMatch(x -> x.getId().equals(v.getId())))
            throw new InvalidOperationException("Duplicate ID detected: " + v.getId());
        fleet.add(v);
        distinctModels.add(v.getModel());
    }

    public boolean removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(v -> v.getId().equals(id));
        if (!removed)
            throw new InvalidOperationException("Vehicle with ID " + id + " not found.");
        rebuildDistinctModelsSet();
        return true;
    }

    public void displayAllVehicles() {
        if (fleet.isEmpty()) {
            System.out.println("Fleet is empty.");
            return;
        }
        for (Vehicle v : fleet) v.displayInfo();
    }

    public List<Vehicle> getFleetSortedByModel() {
        List<Vehicle> sorted = new ArrayList<>(fleet);
        sorted.sort(new SortByModel());
        return sorted;
    }

    public List<Vehicle> getFleetSortedBySpeed() {
        List<Vehicle> sorted = new ArrayList<>(fleet);
        sorted.sort(new SortBySpeed());
        return sorted;
    }

    public List<Vehicle> getFleetSortedByEfficiency() {
        List<Vehicle> sorted = new ArrayList<>(fleet);
        sorted.sort(new SortByEfficiency());
        return sorted;
    }

    public Optional<Vehicle> getFastestVehicle() {
        return fleet.stream().max(Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    public Optional<Vehicle> getSlowestVehicle() {
        return fleet.stream().min(Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        return fleet.stream().filter(v -> {
            try {
                if (v instanceof Maintainable m) return m.needsMaintenance();
                else return v.getCurrentMileage() > 10000;
            } catch (Exception e) {
                return v.getCurrentMileage() > 10000;
            }
        }).collect(Collectors.toList());
    }

    public void maintainAll() {
        for (Vehicle v : fleet) {
            try {
                if (v instanceof Maintainable m) {
                    if (m.needsMaintenance()) {
                        m.performMaintenance();
                        v.setLastMaintenanceMileage(v.getCurrentMileage());
                    }
                } else if (v.getCurrentMileage() > 10000) {
                    System.out.println("Performing generic maintenance for " + v.getId());
                    v.setCurrentMileage(0);
                    v.setLastMaintenanceMileage(0);
                    System.out.println("Maintenance completed for " + v.getClass().getSimpleName() + ": " + v.getId());
                }
            } catch (Exception ex) {
                System.err.println("Maintenance failed for " + v.getId() + ": " + ex.getMessage());
            }
        }

        saveToFile("fleet_data.csv");
        System.out.println("Maintenance completed.");
    }

    public void saveToFile(String filename) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            pw.println("Type,ID,Model,MaxSpeed,Efficiency,CurrentMileage,HasSail");
            for (Vehicle v : fleet) {
                String type = v.getClass().getSimpleName();
                boolean hasSail = (v instanceof CargoShip) && ((CargoShip) v).isSailPowered();
                pw.printf("%s,%s,%s,%.2f,%.2f,%.2f,%b%n",
                        type, v.getId(), v.getModel(),
                        v.getMaxSpeed(), v.calculateFuelEfficiency(), v.getCurrentMileage(), hasSail);
            }
            System.out.println("Fleet saved to " + filename + " (" + fleet.size() + " vehicles).");
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            System.err.println("File not found: " + filename);
            return;
        }

        List<Vehicle> loaded = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (!headerSkipped && line.toLowerCase().startsWith("type,")) {
                    headerSkipped = true;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 7) continue;

                String type = data[0].trim();
                String id = data[1].trim();
                String model = data[2].trim();
                double speed = Double.parseDouble(data[3].trim());
                double mileage = Double.parseDouble(data[5].trim());
                boolean hasSail = Boolean.parseBoolean(data[6].trim());

                Vehicle v = createVehicleFromCSV(type, id, model, speed, hasSail);
                if (v != null) {
                    v.setCurrentMileage(mileage);
                    loaded.add(v);
                }
            }

            fleet.clear();
            fleet.addAll(loaded);
            rebuildDistinctModelsSet();
            System.out.println("Fleet loaded from " + filename + " (" + fleet.size() + " vehicles).");

        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }


    //add from external fleet data
    public void addFromExternalFile(String filename) { 
        File f = new File(filename);
        if (!f.exists()) {
            System.err.println("File not found: " + filename);
            return;
        }

        int addedCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (!headerSkipped && line.toLowerCase().startsWith("type,")) {
                    headerSkipped = true;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 7) continue;

                String type = data[0].trim();
                String id = data[1].trim();
                String model = data[2].trim();
                double speed = Double.parseDouble(data[3].trim());
                double mileage = Double.parseDouble(data[5].trim());
                boolean hasSail = Boolean.parseBoolean(data[6].trim());

                Vehicle v = createVehicleFromCSV(type, id, model, speed, hasSail);
                if (v != null) {
                    v.setCurrentMileage(mileage);
                    try {
                        addVehicle(v);
                        addedCount++;
                    } catch (InvalidOperationException ignored) {}
                }
            }

            saveToFile("fleet_data.csv");
            System.out.println("Successfully added " + addedCount + " new vehicles from " + filename);
            loadFromFile("fleet_data.csv");
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private Vehicle createVehicleFromCSV(String type, String id, String model, double maxSpeed, boolean hasSail) {
        type = (type == null) ? "" : type.trim().toLowerCase();
        switch (type) {
            case "car":
                return new Car(id, model, maxSpeed, 4);
            case "truck":
                return new Truck(id, model, maxSpeed, 6);
            case "bus":
                return new Bus(id, model, maxSpeed, 6);
            case "airplane":
            case "plane":
                return new Airplane(id, model, maxSpeed, 10000.0);
            case "cargoship":
            case "cargo ship":
            case "ship":
                return new CargoShip(id, model, maxSpeed, hasSail);
            default:
                return null;
        }
    }

    public void rebuildDistinctModelsSet() {
        distinctModels.clear();
        for (Vehicle v : fleet) distinctModels.add(v.getModel());
    }

    public int getFleetSize() {
        return fleet.size();
    }

    public Set<String> getDistinctModels() {
        return Collections.unmodifiableSet(distinctModels);
    }

    public void saveOnExit() {
        saveToFile("fleet_data.csv");
    }
}
