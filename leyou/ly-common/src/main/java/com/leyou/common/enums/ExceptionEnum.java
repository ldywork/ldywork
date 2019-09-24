package com.leyou.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGORY_NOT_FUND(404,"商品分类没有查到"),
    BRAND_NOT_FOUND(404,"品牌没有查询到"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组没有查询到"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数没有查询到"),
    GOODS_NOT_FOUND(404,"商品规格参数没有查询到"),
    GOODS_DETAIL_NOT_FOUND(404,"商品详情不存在"),
    GOODS_SKU_NOT_FOUND(404,"商品详情不存在"),
    GOODS_STOCK_NOT_FOUND(404,"商品详情不存在"),
    BRAND_SAVE_ERROR(500,"品牌保存失败"),
    GOODS_SAVE_ERROR(500,"新增保存失败"),
    UPLOAD_FILE_ERROR(500,"上传文件失败");
    private int code;
    private String message;

}
