package it.polito.se2.g04.thesismanagement.attachment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Attachment{
    public Attachment(byte[] bytes, String contentType, String fileName) {
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] bytes;
    private String contentType;
    private String fileName;
    @Id
    @GeneratedValue
    private Long attachmentId;

    public AttachmentDTO toDto(){
        return new AttachmentDTO(this.attachmentId,this.fileName);
    }
}