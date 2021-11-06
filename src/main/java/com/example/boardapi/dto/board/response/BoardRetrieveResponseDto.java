package com.example.boardapi.dto.board.response;

import com.example.boardapi.domain.enumtype.BoardType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel(description = "게시글 조회 응답 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@EqualsAndHashCode(of = "id")
public class BoardRetrieveResponseDto {

    @ApiModelProperty(required = true, value = "게시글 PK", example = "2")
    private Long id;

    @ApiModelProperty(required = true, value = "게시글 작성자", example = "김상운")
    private String author;

    @ApiModelProperty(required = true, value = "게시글 제목", example = "안녕하세요!")
    private String title;

    @ApiModelProperty(required = true, value = "게시글 작성 시간")
    private LocalDateTime createdDate;

    @ApiModelProperty(required = true, value = "게시글 수정 시간")
    private LocalDateTime lastModifiedDate;

    @ApiModelProperty(required = true, value = "좋아요")
    private int likes;

    @ApiModelProperty(required = true, value = "좋아요")
    private int comments;

    @ApiModelProperty(required = true, value = "게시글 유형")
    private BoardType boardType;
}
