package com.lt.comment.controller.vo;

import com.lt.model.comment.SubComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lhz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSubCommentVO {

    String fId;
    List<SubComment> subComments;
}
