package com.app.service;

import com.app.entities.Reservation;

public interface PaymentService {
    public void processPayment(Reservation reservation);
    public void sendMonthlyRevenueReport();
}
