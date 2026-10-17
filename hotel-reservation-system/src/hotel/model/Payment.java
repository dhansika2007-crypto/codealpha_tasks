package hotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simulated payment record. No real payment gateway is used.
 */
public class Payment {
    private final String paymentId;
    private final String bookingId;
    private final double amount;
    private final PaymentMethod method;
    private PaymentStatus status;
    private final LocalDateTime paidAt;
    private String receiptNote;

    public Payment(String paymentId, String bookingId, double amount,
                   PaymentMethod method, PaymentStatus status,
                   LocalDateTime paidAt, String receiptNote) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.paidAt = paidAt;
        this.receiptNote = receiptNote;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public String getReceiptNote() {
        return receiptNote;
    }

    public void setReceiptNote(String receiptNote) {
        this.receiptNote = receiptNote;
    }

    public String toReceipt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("================ PAYMENT RECEIPT ================\n");
        sb.append("Payment ID   : ").append(paymentId).append('\n');
        sb.append("Booking ID   : ").append(bookingId).append('\n');
        sb.append(String.format("Amount Paid  : Rs. %.2f%n", amount));
        sb.append("Method       : ").append(method).append('\n');
        sb.append("Status       : ").append(status).append('\n');
        sb.append("Date & Time  : ").append(paidAt.format(fmt)).append('\n');
        sb.append("Note         : ").append(receiptNote).append('\n');
        sb.append("This is a simulated receipt. No real money was charged.\n");
        sb.append("=================================================");
        return sb.toString();
    }
}
