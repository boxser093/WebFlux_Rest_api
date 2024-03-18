package net.ilya.restcontrollerv100.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("events")
public class EventEntity {

    @Id
    private Long id;
    @Column("userId")
    private Long userId;
    @Column("fileId")
    private Long fileId;
    @Column("status")
    private StatusEntity status;

    @Getter
    @Transient
    private UserEntity userEntity;
    @Getter
    @Transient
    private FileEntity fileEntity;
}
