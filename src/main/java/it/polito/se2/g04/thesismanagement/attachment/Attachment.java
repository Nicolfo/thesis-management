package it.polito.se2.g04.thesismanagement.attachment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Attachment{
    public Attachment(byte[] attachment, String contentType, String fileName) {
        this.attachment = attachment;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    @Lob
    private byte[] attachment;
    private String contentType;
    private String fileName;
    @Id
    @GeneratedValue
    private Long attachmentId;

    public AttachmentDTO toDto(){
        return new AttachmentDTO(this.attachmentId,this.fileName);
    }
}