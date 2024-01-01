package model;

import java.sql.Date;

public class Receipt {
    private int receiptID;
    private int receiptOrder;
    private double receiptAmount;
    private Date paymentDate;
    private String paymentType;

    public Receipt() {
    }

    public Receipt(int receiptID, int receiptOrder, double receiptAmount, Date paymentDate, String paymentType) {
        this.receiptID = receiptID;
        this.receiptOrder = receiptOrder;
        this.receiptAmount = receiptAmount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
    }

    public int getReceiptID() {
        return receiptID;
    }

    public void setReceiptID(int receiptID) {
        this.receiptID = receiptID;
    }

    public int getReceiptOrder() {
        return receiptOrder;
    }

    public void setReceiptOrder(int receiptOrder) {
        this.receiptOrder = receiptOrder;
    }

    public double getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(double receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}