AP-M2025 Assignment 2 — Fleet Management with Collections and File Handling
Mayank Rana
2024341
CSE201

1. Overview
This assignment extends the Fleet Management System developed in Assignment 1 by introducing Java Collections Framework and File I/O (persistence).
The system now behaves as a data-driven application, allowing dynamic vehicle management, sorting, analysis, and persistence between program runs.

2. Compilation & Execution Instructions
Step 1: Compile FROM PROJECT ROOT
javac -d out src\fleet\*.java src\vehicles\*.java src\interfaces\*.java src\exceptions\*.java src\util\*.java

Step 2: Run
java -cp out fleet.Main

Alternatively, run 'run.bat' (included) for automatic compilation and execution.

3. Features Implemented
- Dynamic storage using ArrayList<Vehicle> and HashSet<String>.
- Unique model tracking and duplicate prevention.
- Sorting by model, speed, and efficiency (sail-powered ships treated as infinitely efficient).
- Full CSV-based persistence with automatic load/save + manual csv upload via option 12.
- Interactive CLI for add/remove/sort/display/refuel/maintenance.
- Analytics report showing averages, fastest/slowest, and composition.
- Maintenance and refueling operations.
- Robust exception handling and validation.

4. Data File Format
File: fleet_data.csv  
Each line represents a single vehicle:
File: Some external csv (same format as fleet_data.csv)
Each line represents a single vehicle:

Type,ID,Model,MaxSpeed,Efficiency,CurrentMileage,HasSail
CargoShip,S1,SHIP1,40.00,0.00,20.00,true
CargoShip,S2,SHIP2,40.00,4.00,0.00,false
Car,C1,WAGONR,180.00,15.00,40000.00,false
Truck,T1,BHARATBRIGADE,85.00,6.00,40000.00,false
Bus,B1,VOLVO,70.00,10.00,40000.00,false
Airplane,A1,BOING,800.00,5.00,40000.00,false
Bus,B2,ASHOKLEYLAND,80.00,10.00,20000.00,false
Airplane,A2,A320,950.00,5.00,20000.00,false
CargoShip,S3,SHIP3,65.00,4.00,0.00,false
Car,C2,LEXUS,320.00,15.00,20000.00,false


5. Collections Used
 1. ArrayList<Vehicle> — dynamic fleet storage.  
 2. HashSet<String> — unique models tracking.  
 3. LinkedHashMap — used in analytics for ordered reporting.  

6. Sorting Mechanism
- SortByModel → A–Z  
- SortBySpeed → Descending (Fastest First)  
- SortByEfficiency → Descending (Most Efficient First, with SAIL-powered first coz infinite efficiency)  

7. Exception Handling
Custom exceptions: InvalidOperationException, InsufficientFuelException, OverloadException.  
All file I/O uses try catch finally blocks.  

8. Sample CLI Output
=== Fleet Management System (Assignment 2) ===  
Fleet loaded from fleet_data.csv (10 vehicles).  
...  
Choice: 3  
ID: S1, Model: SHIP1 (SAIL), Max Speed: 40.0 km/h, Efficiency: ∞ (wind-powered), Mileage: 0.0 km  
...  

9. Design Notes
- Reuses Assignment 1 hierarchy (Vehicle → subclasses).  
- Implements new Comparators in src/util (speed,efficeincy and model sorting).  
- Strong encapsulation; extensible for new types.  

10. Submission Contents
- src/ (all Java source files)  
- fleet_data.csv (sample data)  
- README.txt  
- run.bat (optional compile/run script)  
