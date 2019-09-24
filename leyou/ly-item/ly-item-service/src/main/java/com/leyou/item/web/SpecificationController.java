package com.leyou.item.web;

import com.leyou.item.SpecGroup;
import com.leyou.item.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(
            @RequestParam(value = "gid" , required = false) Long gid,
            @RequestParam(value = "cid" , required = false) Long cid,
            @RequestParam(value = "gid" , required = false) Boolean searching
            ){
        return ResponseEntity.ok(specificationService.queryParamList(gid,cid,searching));
    }
}
