package com.leyou.item;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

public class pojo {
    @Data
    @Table(name="tb_category")
    public static class Category {
        @Id
        @KeySql(useGeneratedKeys=true)
        private Long id;
        private String name;
        private Long parentId;
        private Boolean isParent;
        private Integer sort;
        // getter和setter略
        // 注意isParent的get和set方法
    }
}
