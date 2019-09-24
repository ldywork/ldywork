package com.leyou.search.repository;


import com.leyou.common.vo.PageResult;
import com.leyou.item.Spu;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsRepositoryTest {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private SearchService searchService;

    @Test
    public void test() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);

    }

    @Test
    public void loadData() {
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            PageResult<Spu> spuPageResult = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> items = spuPageResult.getItems();
            if (CollectionUtils.isEmpty(items)) {
                break;
            }
//            SearchService searchService = new SearchService();
            List<Goods> collect = new ArrayList<>();
            for(Spu spu :items){
                Goods goods = searchService.getGoods(spu);
                collect.add(goods);
            }
            goodsRepository.saveAll(collect);
            page++;

            size = items.size();
        } while (size == 100);
    }

}
