//package com.app.Util;
//
//
//import com.app.entities.Reservation;
//import com.app.entities.ReservationStatus;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.database.JdbcBatchItemWriter;
//import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
//import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
//import org.springframework.batch.item.database.JdbcCursorItemReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//
//import javax.sql.DataSource;
//import java.time.LocalDate;
//
//@Configuration
//@EnableBatchProcessing
//public class ReservationBatchConfig {
//
//    @Autowired
//    private JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;
//
//    @Autowired
//    private DataSource dataSource;
//
//    // Reader: Fetch reservations that are COMPLETED and checked out before today.
//    @Bean
//    public JdbcCursorItemReader<Reservation> reservationReader() {
//        String sql = "SELECT * FROM reservation WHERE status = 'COMPLETED' AND check_out_date < ?";
//        return new JdbcCursorItemReaderBuilder<Reservation>()
//                .dataSource(dataSource)
//                .name("reservationReader")
//                .sql(sql)
//                .preparedStatementSetter(ps -> ps.setDate(1, java.sql.Date.valueOf(LocalDate.now())))
//                .rowMapper(new BeanPropertyRowMapper<>(Reservation.class))  // Create a custom row mapper or use BeanPropertyRowMapper.
//                .build();
//    }
//
//    // Processor: You can add custom business logic here. In this example, we simply mark the reservation as archived.
//    @Bean
//    public ItemProcessor<Reservation, Reservation> reservationProcessor() {
//        return reservation -> {
//            // For example, set an archived flag or update some status.
//            // Assume you have added an 'archived' field or simply update the status.
//            reservation.setStatus(ReservationStatus.ARCHIVED);
//            return reservation;
//        };
//    }
//
//    // Writer: Update the reservation record in the database.
//    @Bean
//    public JdbcBatchItemWriter<Reservation> reservationWriter() {
//        return new JdbcBatchItemWriterBuilder<Reservation>()
//                .dataSource(dataSource)
//                .sql("UPDATE reservation SET status = :status WHERE reservation_id = :reservationId")
//                .beanMapped()
//                .build();
//    }
//
//    // Define the processing step.
//    @Bean
//    public Step reservationArchivingStep() {
//        return stepBuilderFactory.get("reservationArchivingStep")
//                .<Reservation, Reservation>chunk(10)
//                .reader(reservationReader())
//                .processor(reservationProcessor())
//                .writer(reservationWriter())
//                .build();
//    }
//
//    // Define the job.
//    @Bean
//    public Job reservationArchivingJob() {
//        return jobBuilderFactory.get("reservationArchivingJob")
//                .incrementer(new RunIdIncrementer())
//                .start(reservationArchivingStep())
//                .build();
//    }
//}
//
