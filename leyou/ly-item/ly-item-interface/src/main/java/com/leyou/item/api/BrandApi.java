package com.leyou.item.api;

import com.leyou.item.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
public interface BrandApi {
    @GetMapping("brand/cid/{cid}")
    List<Brand> queryBrandListByCid(@PathVariable("cid")Long cid);
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable("id") Long id);
}
