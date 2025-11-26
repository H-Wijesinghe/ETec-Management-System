package lk.ijse.etecmanagementsystem.util;

public class ButtonActions {

    public  static void performAction(String actionKey) {
        // Implement action handling logic based on actionKey
        switch (actionKey) {
            case "SAVE":
                // Code to save data
                System.out.println("Saving data...");
                break;
            case "DELETE":
                // Code to delete data
                System.out.println("Deleting data...");
                break;
            case "UPDATE":
                // Code to update data
                System.out.println("Updating data...");
                break;
            default:
                System.out.println("Unknown action: " + actionKey);
        }
    }
}
