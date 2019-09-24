package com.leyou.item.api;

import com.leyou.item.pojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
public interface CategoryApi {
    @GetMapping("category/list/ids")
    ResponseEntity<List<pojo.Category>> queryCategoryListByPid(@RequestParam("pid") Long pid);
    @GetMapping("category/list/ids")
    List<pojo.Category> queryCategoryListByids(@RequestParam("ids") List<Long> ids);
}
