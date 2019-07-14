package com.haojg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_demo")
public class Demo extends Model<Demo> {

    /**
     * 主键Id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

}
