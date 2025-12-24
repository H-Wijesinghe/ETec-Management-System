package lk.ijse.etecmanagementsystem.dto;

public class TopProductDTO {
    private int rank;
    private String name;
    private double popularity; // 0.0 to 1.0 for ProgressBar
    private String salesPercentageText;

    public TopProductDTO(int rank, String name, double popularity, String salesPercentageText) {
        this.rank = rank;
        this.name = name;
        this.popularity = popularity;
        this.salesPercentageText = salesPercentageText;
    }

    // Getters
    public int getRank() { return rank; }
    public String getName() { return name; }
    public double getPopularity() { return popularity; }
    public String getSalesPercentageText() { return salesPercentageText; }
}
