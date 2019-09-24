package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.*;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        PageHelper.startPage(page,rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(null != saleable){
            criteria.andEqualTo("saleable",saleable);
        }
        example.setOrderByClause("last_update_time desc");
        List<Spu> spus = spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        loadCategoryAndBrandName(spus);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spus);
        return new PageResult<>(spuPageInfo.getTotal(),spus);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for(Spu spu : spus){
            List<String> collect = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).
                    stream().map(pojo.Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(collect,"/"));
            spu.setBname(brandService.queryBrandById(spu.getBrandId()).getName());
        }
    }

    public void saveGoods(Spu spu) {
        //保存spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(new Date());
        spu.setSaleable(true);
        spu.setValid(false);
        int insert = spuMapper.insert(spu);
        if (insert != 1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insert(spuDetail);
        saveSkuAndStock(spu);

    }

    private void saveSkuAndStock(Spu spu) {
        int insert;//新增sku
        List<Stock> stockList = new ArrayList<>();
        List<Sku> skus = spu.getSkus();

        for(Sku sku : skus){
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            sku.setSpuId(spu.getId());
            insert = skuMapper.insertSelective(sku);
            if (1 != insert){
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        if (1 <= stockList.size()){
            stockMapper.insertList(stockList);
        }
    }

    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (null == spuDetail){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
        return spuDetail;

    }

    public List<Sku> querySkuBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> select = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(select)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        for(Sku sku1 : select){
            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
            if (null == stock){
                throw new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
            }
        }
        return select;
    }

    @Transactional
    public void updateGoods(Spu spu) {
        // 查询以前sku
        List<Sku> skus = this.querySkuBySpuId(spu.getId());
        // 如果以前存在，则删除
        if(!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            this.skuMapper.delete(record);

        }


        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        // 新增sku和库存
        saveSkuAndStock(spu);
    }
}
