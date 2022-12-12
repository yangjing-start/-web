package com.lt.comment.controller.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @author Lhz
 */
@ToString
@Data
public class InsertHistoryDTO {
    @NotBlank
    @Size(min = 6, max = 6)
    String username;
    @NotBlank
    @Size(max = 10)
    String debateId;
    @NotBlank
    @Size(max = 100)
    String debateNickname;
    @NotBlank
    @Size(max = 300)
    String userImage;
    @NotBlank
    @Size(max = 100)
    String title;
}
