package com.konstantinov.onlinestore.bd.goods;

import com.konstantinov.onlinestore.rest.dto.CakeDetail;
import com.konstantinov.onlinestore.rest.dto.Cakes;

public interface CakeService {
    Cakes getCakes();
    void addCake(CakeDetail cake);
    CakeDetail getCakeById(Long id);
}
