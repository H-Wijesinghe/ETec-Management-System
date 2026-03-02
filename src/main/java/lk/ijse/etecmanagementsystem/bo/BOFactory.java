package lk.ijse.etecmanagementsystem.bo;


import lk.ijse.etecmanagementsystem.bo.custom.impl.CategoryBOImpl;
import lk.ijse.etecmanagementsystem.bo.custom.impl.CustomerBOImpl;
import lk.ijse.etecmanagementsystem.bo.custom.impl.DashboardBOImpl;
import lk.ijse.etecmanagementsystem.bo.custom.impl.InventoryBOImpl;

public class BOFactory {
    private static BOFactory instance;

    private BOFactory() {}

    public static BOFactory getInstance() {
        if (instance == null) {
            instance = new BOFactory();
        }
        return instance;
    }

    public enum BOTypes {
        CATEGORY, CUSTOMER, DASHBOARD, INVENTORY
    }

    public SuperBO getBO(BOTypes boType) {
        switch (boType) {
            case CATEGORY:
                return new CategoryBOImpl();
            case CUSTOMER:
                return new CustomerBOImpl();
            case DASHBOARD:
                return new DashboardBOImpl();
            case INVENTORY:
                return new InventoryBOImpl();
            default:
                return null;
        }
    }
}
