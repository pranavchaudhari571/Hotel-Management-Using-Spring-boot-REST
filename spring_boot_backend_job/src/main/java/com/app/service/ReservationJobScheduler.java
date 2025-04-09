//package com.app.service;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//public class ReservationJobScheduler {
//
//    @Autowired
//    private JobLauncher jobLauncher;
//
//    @Autowired
//    private Job reservationArchivingJob;
//
//    // Cron expression: At 2:00 AM every day.
//    @Scheduled(cron = "0 0 11 * * ?")
//    public void runReservationArchivingJob() {
//        log.info("Starting scheduled reservation archiving job...");
//        JobParameters params = new JobParametersBuilder()
//                .addLong("run.id", System.currentTimeMillis())
//                .toJobParameters();
//        try {
//            jobLauncher.run(reservationArchivingJob, params);
//            log.info("Reservation archiving job completed successfully.");
//        } catch (Exception e) {
//            log.error("Reservation archiving job failed: ", e);
//        }
//    }
//}
//
