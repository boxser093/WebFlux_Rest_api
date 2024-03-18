package net.ilya.restcontrollerv100.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("files")
public class FileEntity {
    @Id
    private Long id;
    @Column("fileName")
    private String fileName;
    @Column("filePath")
    private String filePath;
    @Column("status")
    private StatusEntity status;
    @Transient
    @ToString.Exclude
    private EventEntity eventEntity;
}
