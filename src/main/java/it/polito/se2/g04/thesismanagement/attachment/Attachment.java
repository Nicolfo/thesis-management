package it.polito.se2.g04.thesismanagement.attachment;

import jakarta.persistence.*;
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

    @Lob @Basic(fetch = FetchType.LAZY)
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