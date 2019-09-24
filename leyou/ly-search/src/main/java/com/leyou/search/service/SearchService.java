package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;

    public Goods getGoods(Spu spu){
        //查询分类
        List<pojo.Category> categories = categoryClient.
                queryCategoryListByids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FUND);
        }
        List<String> collect = categories.stream().map(pojo.Category::getName).collect(Collectors.toList());
        //查询品牌信息
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (null == brand){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //标题+分类+品牌名称
        String all = spu.getTitle()+ StringUtils.join(collect," ")+brand.getName();
        //查询sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //sku价格集合
        List<Long> collect1 = skus.stream().map(Sku::getPrice).collect(Collectors.toList());
        List<Map<String, Object>> skuList = new ArrayList<>();
        for(Sku sku : skus){
            Map<String, Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));
            skuList.add(map);
        }
        Map<String,Object> specs = new HashMap<>();
        //商品的规格参数的key
        List<SpecParam> specParams = specificationClient.queryParamByGid(null, spu.getCid3(), null);
        if (CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询spu的detail
        SpuDetail spuDetail = goodsClient.queryDetailById(spu.getId());
        Map<Long, String> longStringMap = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        Map<Long, List<String>> longListMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(),
                new TypeReference<Map<Long, List<String>>>() {});
        for(SpecParam specParam :specParams){
            String key = specParam.getName();
            Object value = "";
            if (specParam.getGeneric()){
                value = longStringMap.get(specParam.getId());
                if (specParam.getNumeric()){
                    value = chooseSegment(value.toString(),specParam);
                }
            }else {
                value = longListMap.get(specParam.getId());
            }
            specs.put(key,value);
        }
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all);
        goods.setPrice(collect1);
        goods.setSkus(JsonUtils.toString(skuList));
        goods.setSpecs(specs);
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        queryBuilder.withPageable(PageRequest.of(page,size));
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));
        Page<Goods> result = goodsRepository.search(queryBuilder.build());
        long totalElements = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        return new PageResult<>(totalElements,totalPages,goodsList);
    }
}
