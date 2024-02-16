package net.ilya.restcontrollerv100.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("events")
public class EventEntity {
    @Id
    private Long id;
    @Column("user_id")
    private UserEntity user;
    @Column("file_id")
    private FileEntity file;
    @Column("status")
    private StatusEntity eventStatus;
}
