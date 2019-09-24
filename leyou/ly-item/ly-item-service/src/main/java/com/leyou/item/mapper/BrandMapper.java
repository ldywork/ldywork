package com.leyou.item.mapper;

import com.leyou.item.Brand;
import com.leyou.item.pojo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand>{
    @Insert("insert into tb_category_brand (category_id,brand_id) values(#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid")Long cid , @Param("bid")Long bid);

    @Select("SELECT b.* from tb_category_brand cd LEFT JOIN tb_brand b on cd.brand_id = b.id WHERE cd.category_id = #{cid}")
    List<Brand> queryBrandListByCid(@Param("cid") Long cid);
}
