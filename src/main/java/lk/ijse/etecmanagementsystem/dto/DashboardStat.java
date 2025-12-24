package lk.ijse.etecmanagementsystem.dto;

public class DashboardStat {
    private String title;
    private String value;
    private String subtitle;
    private double growthPercentage;

    public DashboardStat(String title, String value, String subtitle, double growthPercentage) {
        this.title = title;
        this.value = value;
        this.subtitle = subtitle;
        this.growthPercentage = growthPercentage;
    }

    // Getters
    public String getTitle() { return title; }
    public String getValue() { return value; }
    public String getSubtitle() { return subtitle; }
    public double getGrowthPercentage() { return growthPercentage; }
}
