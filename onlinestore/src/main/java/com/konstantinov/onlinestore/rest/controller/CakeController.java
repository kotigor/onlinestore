package com.konstantinov.onlinestore.rest.controller;

import com.konstantinov.onlinestore.dto.Cake;
import com.konstantinov.onlinestore.dto.Cakes;
import com.konstantinov.onlinestore.exception.CakeNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class CakeController {
    private final Cakes cakeList = new Cakes();
    private Long id = 3L;

    public CakeController() {
        Cake cake1 = new Cake();
        cake1.setName("Napoleon");
        cake1.setPrice(new BigDecimal(100));
        cake1.setWeight(new BigDecimal(100));
        cake1.setImage("cake1.jpg");
        cake1.setCalories(new BigDecimal(100));
        Cake cake2 = new Cake();
        cake2.setName("Rose");
        cake2.setPrice(new BigDecimal(200));
        cake2.setWeight(new BigDecimal(200));
        cake2.setImage("cake1.jpg");
        cake2.setCalories(new BigDecimal(200));
        List<Cake> tmp = new ArrayList<Cake>();
        cake1.setId(1L);
        cake2.setId(2L);
        tmp.add(cake1);
        tmp.add(cake2);
        cakeList.setCakeList(tmp);
    }

    @GetMapping(value = "cakes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Cakes cakes() {
        return cakeList;
    }

    @GetMapping(value = "cake/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Cake getCakeById(@PathVariable Long id) {
        return cakeList.getCakeList().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new CakeNotFoundException("Cake not exist"));
    }

    @PostMapping(value = "cake", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addCake(@Valid @RequestBody Cake cake){
        cake.setId(id);
        cakeList.getCakeList().add(cake);
        id++;
    }
}
