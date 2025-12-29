package lk.ijse.etecmanagementsystem.dto.tm;

public class DashboardTM {
    private final double todayIncome;
    private final int activeRepairs;
    private final int lowStock;
    private final double pendingPayments;

    public DashboardTM(double todayIncome, int activeRepairs, int lowStock, double pendingPayments) {
        this.todayIncome = todayIncome;
        this.activeRepairs = activeRepairs;
        this.lowStock = lowStock;
        this.pendingPayments = pendingPayments;
    }

    public double getTodayIncome() { return todayIncome; }
    public int getActiveRepairs() { return activeRepairs; }
    public int getLowStock() { return lowStock; }
    public double getPendingPayments() { return pendingPayments; }
}