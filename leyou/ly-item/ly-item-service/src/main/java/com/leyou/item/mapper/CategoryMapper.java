package com.leyou.item.mapper;

import com.leyou.item.pojo;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface CategoryMapper extends Mapper<pojo.Category>,IdListMapper<pojo.Category,Long>{
}
