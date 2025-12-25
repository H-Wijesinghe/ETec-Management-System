package lk.ijse.etecmanagementsystem.dto.tm;

import javafx.beans.property.*;
import lk.ijse.etecmanagementsystem.dto.RepairJobDTO;
import lk.ijse.etecmanagementsystem.util.RepairStatus;
import java.text.SimpleDateFormat;

public class RepairJobTM {

    // Properties for UI Binding
    private final IntegerProperty repairId;
    private final StringProperty customerName; // Note: DTO doesn't have name, we add it here for UI
    private final StringProperty contactNumber;
    private final StringProperty deviceName;
    private final StringProperty serialNumber;
    private final StringProperty problemDescription;
    private final ObjectProperty<RepairStatus> status;
    private final StringProperty dateInFormatted; // Pre-formatted date for display

    // Keep reference to original DTO for Database operations
    private RepairJobDTO originalDto;

    public RepairJobTM(RepairJobDTO dto, String cusName, String cusContact) {
        this.originalDto = dto;

        this.repairId = new SimpleIntegerProperty(dto.getRepairId());
        this.customerName = new SimpleStringProperty(cusName);
        this.contactNumber = new SimpleStringProperty(cusContact);
        this.deviceName = new SimpleStringProperty(dto.getDeviceName());
        this.serialNumber = new SimpleStringProperty(dto.getDevice_sn());
        this.problemDescription = new SimpleStringProperty(dto.getProblem_desc());
        this.status = new SimpleObjectProperty<>(dto.getStatus());

        // Format Date for UI
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = (dto.getDateIn() != null) ? sdf.format(dto.getDateIn()) : "N/A";
        this.dateInFormatted = new SimpleStringProperty(dateStr);
    }

    // --- Getters for Properties (Used by JavaFX) ---
    public IntegerProperty repairIdProperty() { return repairId; }
    public StringProperty customerNameProperty() { return customerName; }
    public StringProperty deviceNameProperty() { return deviceName; }
    public StringProperty serialNumberProperty() { return serialNumber; }
    public StringProperty problemDescriptionProperty() { return problemDescription; }
    public ObjectProperty<RepairStatus> statusProperty() { return status; }
    public StringProperty dateInFormattedProperty() { return dateInFormatted; }
    public StringProperty contactNumberProperty() { return contactNumber; }

    // --- Regular Getters/Setters ---
    public RepairStatus getStatus() { return status.get(); }

    public void setStatus(RepairStatus newStatus) {
        this.status.set(newStatus);
        this.originalDto.setStatus(newStatus); // Sync with DTO
    }

    public RepairJobDTO getOriginalDto() {
        // Sync text fields back to DTO before saving
        originalDto.setProblem_desc(problemDescription.get());
        return originalDto;
    }
}