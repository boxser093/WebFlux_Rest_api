package net.ilya.restcontrollerv100.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
@Builder
public class FileUpload {
    private String name;
    private String url;
}
