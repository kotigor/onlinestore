package com.konstantinov.onlinestore.bd.goods;

import com.konstantinov.onlinestore.rest.dto.Cake;
import com.konstantinov.onlinestore.rest.dto.CakeDetail;
import com.konstantinov.onlinestore.rest.dto.Cakes;
import com.konstantinov.onlinestore.rest.dto.Order;

import java.util.List;

public interface CakeService {
    Cakes getCakes();
    CakeDetail getCakeById(Long id);
    List<Cake> getSomeCake(Integer page, Integer size);
    void updateCake(Cake cake);
    void updateOrCreateCakeDetail(CakeDetail cakeDetail);
    void getAnyCake() throws InterruptedException;
}
