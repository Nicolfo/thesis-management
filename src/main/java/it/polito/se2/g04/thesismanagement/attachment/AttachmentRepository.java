package it.polito.se2.g04.thesismanagement.attachment;

import it.polito.se2.g04.thesismanagement.attachment.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment,Long> {
}
