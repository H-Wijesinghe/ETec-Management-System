package lk.ijse.etecmanagementsystem.dto.tm; // Adjust package

public class UrgentRepairTM {
    private final int id;
    private final String device;
    private final String status;
    private final String date;

    public UrgentRepairTM(int id, String device, String status, String date) {
        this.id = id;
        this.device = device;
        this.status = status;
        this.date = date;
    }

    public int getId() { return id; }
    public String getDevice() { return device; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
}