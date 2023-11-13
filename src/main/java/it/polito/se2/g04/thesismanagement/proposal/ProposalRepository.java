package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProposalRepository extends JpaRepository<Proposal,Long> {
}
