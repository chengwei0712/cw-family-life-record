package com.cw.picture.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 日记媒体关联表
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Data
@TableName("diary_media")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryMedia implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日记 ID
     */
    private Long diaryId;

    /**
     * 媒体 ID
     */
    private Long mediaId;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
