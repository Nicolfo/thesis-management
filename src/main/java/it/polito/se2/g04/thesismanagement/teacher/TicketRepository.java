package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Teacher,Long> {
}
