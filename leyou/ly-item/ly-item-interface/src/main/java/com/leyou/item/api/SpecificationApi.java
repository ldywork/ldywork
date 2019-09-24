package com.leyou.item.api;

import com.leyou.item.SpecParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
public interface SpecificationApi {
    @GetMapping("spec/params")
    List<SpecParam> queryParamByGid(
            @RequestParam(value = "gid" , required = false) Long gid,
            @RequestParam(value = "cid" , required = false) Long cid,
            @RequestParam(value = "gid" , required = false) Boolean searching
    );
}
