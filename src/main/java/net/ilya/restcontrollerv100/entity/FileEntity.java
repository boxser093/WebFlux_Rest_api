package net.ilya.restcontrollerv100.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("files")
public class FileEntity {
    @Id
    private Long id;
    private String fileName;
    private String filePath;
    private StatusEntity fileStatus;
}
