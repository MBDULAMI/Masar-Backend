
package com.maryam.masar.repository;

import com.maryam.masar.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByBooking_Id(Long bookingId);
}
