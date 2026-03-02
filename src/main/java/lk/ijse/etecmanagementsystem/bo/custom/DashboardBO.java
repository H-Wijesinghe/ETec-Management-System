package lk.ijse.etecmanagementsystem.bo.custom;

import javafx.scene.chart.XYChart;
import lk.ijse.etecmanagementsystem.bo.SuperBO;
import lk.ijse.etecmanagementsystem.dto.CustomDTO;

import java.sql.SQLException;
import java.util.List;

public interface DashboardBO extends SuperBO {

    CustomDTO getDashboardStats() throws SQLException;

    List<CustomDTO> getUrgentRepairs() throws SQLException;

    List<CustomDTO> getUnpaidDebts() throws SQLException;

    XYChart.Series<String, Number> getSalesChartData() throws SQLException;

    List<XYChart.Series<String, Number>> getTrafficChartData() throws SQLException;

}
