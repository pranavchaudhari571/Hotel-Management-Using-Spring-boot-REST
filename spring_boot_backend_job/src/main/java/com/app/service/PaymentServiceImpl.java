package com.app.service;

import com.app.dao.PaymentRepository;
import com.app.entities.Payment;
import com.app.entities.PaymentStatus;
import com.app.entities.Reservation;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@XSlf4j
public class PaymentServiceImpl implements PaymentService{
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private NotificationService notificationService;

    @Async("asyncTaskExecutor")
//    @Scheduled(cron = "0 0 10 5 * ?")
 @Scheduled(cron = "0 */10 * * * ?")
//Runs on the 5th of every month at 10 AM
    public void sendMonthlyRevenueReport() {
        log.info("Starting monthly revenue report generation...");

        // Get the previous month
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();

        log.info("Fetching payments for reservations with check-in dates between {} and {}", startDate, endDate);

        // Fetch payments based on reservation's check-in date
        List<Payment> payments = paymentRepository.findPaymentsByCheckInDate(startDate, endDate);

        // Calculate total revenue
        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Total revenue for {}: {}", previousMonth, totalRevenue);

        // Generate the email report
        String emailBody = generateEmailReport(previousMonth, totalRevenue, payments);

        // Send email to admin
        String adminEmail = "pranavprem1613@gmail.com";  // Replace with actual admin email
        try {
            notificationService.sendHtmlEmail(adminEmail, "Monthly Revenue Report - " + previousMonth, emailBody);
            log.info("Monthly revenue report sent successfully to {}", adminEmail);
        } catch (MessagingException e) {
            log.error("Failed to send monthly revenue report: {}", e.getMessage());
        }
    }
    private String generateEmailReport(YearMonth month, BigDecimal revenue, List<Payment> payments) {
        StringBuilder report = new StringBuilder();

        report.append("<html><head>");
        report.append("<style>");
        report.append("body { font-family: Arial, sans-serif; padding: 20px; color: #333; }");
        report.append("h2 { text-align: center; color: #007bff; }");
        report.append(".container { max-width: 600px; margin: auto; padding: 20px; background: #f9f9f9; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); }");
        report.append(".summary { background: #007bff; color: white; padding: 15px; border-radius: 5px; font-size: 18px; text-align: center; font-weight: bold; }");
        report.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; }");
        report.append("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }");
        report.append("th { background: #007bff; color: white; }");
        report.append("</style>");
        report.append("</head><body>");

        report.append("<div class='container'>");
        report.append("<h2>📊 Monthly Revenue Report - ").append(month).append("</h2>");
        report.append("<div class='summary'>Total Revenue: ₹").append(revenue).append("</div>");

        // **1️⃣ Generate Chart Image URL**
        String chartUrl = generateChartUrl(payments);

        // **2️⃣ Embed Chart Image**
        report.append("<div style='text-align: center;'><img src='").append(chartUrl).append("' alt='Revenue Chart' width='500'/></div>");

        // **3️⃣ Table Data**
        report.append("<table>");
        report.append("<thead><tr><th>Reservation ID</th><th>Amount</th><th>Check-in Date</th></tr></thead><tbody>");

        for (Payment payment : payments) {
            report.append("<tr>")
                    .append("<td>").append(payment.getReservation().getReservationId()).append("</td>")
                    .append("<td>₹").append(payment.getAmount()).append("</td>")
                    .append("<td>").append(payment.getReservation().getCheckInDate()).append("</td>")
                    .append("</tr>");
        }

        report.append("</tbody></table>");
        report.append("</div></body></html>");

        return report.toString();
    }


    private String generateChartUrl(List<Payment> payments) {
        StringBuilder labels = new StringBuilder();
        StringBuilder data = new StringBuilder();

        for (Payment payment : payments) {
            labels.append("\"").append(payment.getReservation().getCheckInDate()).append("\",");
            data.append(payment.getAmount()).append(",");
        }

        // Remove last comma
        if (labels.length() > 0) labels.setLength(labels.length() - 1);
        if (data.length() > 0) data.setLength(data.length() - 1);

        return "https://quickchart.io/chart?c={"
                + "\"type\":\"bar\","
                + "\"data\":{"
                + "\"labels\":[" + labels + "],"
                + "\"datasets\":[{\"label\":\"Revenue (₹)\",\"data\":[" + data + "],"
                + "\"backgroundColor\":\"rgba(54, 162, 235, 0.6)\"}]"
                + "},\"options\":{"
                + "\"responsive\":true,"
                + "\"plugins\":{"
                + "\"legend\":{ \"display\":true }"
                + "}"
                + "}}";
    }




    public void processPayment(Reservation reservation) {
        Payment payment = new Payment();
        log.info("Creating payment for reservationId: {}", reservation.getReservationId());
        payment.setReservation(reservation);  // Should now have the correct reservationId
        payment.setAmount(calculateAmount(reservation));
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);
    }


    private BigDecimal calculateAmount(Reservation reservation) {
        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        BigDecimal pricePerDay = reservation.getRoom().getPrice();
        BigDecimal daysInBigDecimal = BigDecimal.valueOf(days);
        return pricePerDay.multiply(daysInBigDecimal);
    }
}
