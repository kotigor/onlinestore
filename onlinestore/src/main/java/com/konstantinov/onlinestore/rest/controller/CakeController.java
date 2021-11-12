package com.konstantinov.onlinestore.rest.controller;

import com.konstantinov.onlinestore.bd.goods.CakeServiceImpl;
import com.konstantinov.onlinestore.rest.dto.Cake;
import com.konstantinov.onlinestore.rest.dto.CakeDetail;
import com.konstantinov.onlinestore.rest.dto.Cakes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class CakeController {
    private final CakeServiceImpl cakeService;

    @Autowired
    public CakeController(CakeServiceImpl cakeService) {
        this.cakeService = cakeService;
    }


    @GetMapping(value = "cakes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Cakes cakes() {
        return cakeService.getCakes();
    }

    @GetMapping(value = "cake/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CakeDetail getCakeById(@PathVariable Long id) {
        return cakeService.getCakeById(id);
    }

    @PostMapping(value = "cake", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addCake(@Valid @RequestBody CakeDetail cake){
        cakeService.addCake(cake);
    }
}
