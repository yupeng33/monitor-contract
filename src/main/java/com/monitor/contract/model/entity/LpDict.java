package com.monitor.contract.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author peng
 * @since 2024-07-28
 */
@Getter
@Setter
@TableName("lp_dict")
public class LpDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer chainId;

    private String name;

    private String value;

    /**
     * 0-失效；1-生效
     */
    private Boolean status;

    private String remark;


}
