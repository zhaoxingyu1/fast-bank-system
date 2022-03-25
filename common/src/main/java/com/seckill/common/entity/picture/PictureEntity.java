package com.seckill.common.entity.picture;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zxy
 * @Classname Picture
 * @Date 2022/3/25 14:28
 */
@TableName("picture")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String pictureId;
    private String pictureUrl;
    private String targetId;
}
