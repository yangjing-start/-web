package com.lt.comment.controller.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @author Lhz
 */
@Data
@ToString
public class DeleteHistoryDTO {
    @NotBlank
    @Size(min = 6, max = 6)
    String username;
    @NotBlank
    @Size( max = 10)
    String debateId;
}
