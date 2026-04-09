package com.cw.picture.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 日记信息
 *
 * @author chengwei
 * @since 2026-04-03
 */
@Data
@TableName("diary_info")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryInfo implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 日记标题
     */
    private String title;

    /**
     * 日记内容
     */
    private String content;

    /**
     * 心情
     */
    private String mood;

    /**
     * 天气
     */
    private String weather;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    private Integer isDeleted;
}
