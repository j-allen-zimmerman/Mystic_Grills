package controller;

import database.Connect;
import model.Receipt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptController {

    public static boolean processPayment(Receipt receipt) {
        String insertReceiptQuery = "INSERT INTO Receipt (receiptOrder, receiptAmount, paymentDate, paymentType) VALUES (?, ?, ?, ?)";
        String updateOrderStatusQuery = "UPDATE Orders SET orderStatus = 'Paid' WHERE orderId = ?";

        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement insertPs = connection.prepareStatement(insertReceiptQuery);
             PreparedStatement updatePs = connection.prepareStatement(updateOrderStatusQuery)) {

            insertPs.setInt(1, receipt.getReceiptOrder());
            insertPs.setDouble(2, receipt.getReceiptAmount());
            insertPs.setDate(3, receipt.getPaymentDate());
            insertPs.setString(4, receipt.getPaymentType());
            insertPs.executeUpdate();
            updatePs.setInt(1, receipt.getReceiptOrder());
            updatePs.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Receipt> getAllReceipts() {
        List<Receipt> receipts = new ArrayList<>();
        String query = "SELECT * FROM Receipt";

        try (Connection connection = Connect.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int receiptID = rs.getInt("receiptID");
                int receiptOrder = rs.getInt("receiptOrder");
                double receiptAmount = rs.getDouble("receiptAmount");
                Date paymentDate = rs.getDate("paymentDate");
                String paymentType = rs.getString("paymentType");

                Receipt receipt = new Receipt(receiptID, receiptOrder, receiptAmount, paymentDate, paymentType);
                receipts.add(receipt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receipts;
    }

}
