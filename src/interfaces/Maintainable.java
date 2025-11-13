package interfaces;

//inf for vehicles than need maintenance
public interface Maintainable {
    void scheduleMaintenance();
    boolean needsMaintenance();//checks
    void performMaintenance();
}
