package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<pojo.Category> queryCategoryListByPid(Long pid) {
        pojo.Category category = new pojo.Category();
        category.setParentId(pid);
        List<pojo.Category> select = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(select)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FUND);
        }
        return select;
    }
    public List<pojo.Category> queryByIds(List<Long> ids){
        List<pojo.Category> categories = categoryMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FUND);
        }
        return  categories;
    }
}
