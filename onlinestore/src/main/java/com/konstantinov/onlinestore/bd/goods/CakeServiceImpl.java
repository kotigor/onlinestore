package com.konstantinov.onlinestore.bd.goods;

import com.konstantinov.onlinestore.bd.orders.OrderEntity;
import com.konstantinov.onlinestore.rest.dto.Cake;
import com.konstantinov.onlinestore.rest.dto.CakeDetail;
import com.konstantinov.onlinestore.rest.dto.Cakes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CakeServiceImpl implements CakeService {
    private final CakeDAO cakeRepository;
    private final CompositionDAO compositionRepository;

    @Autowired
    public CakeServiceImpl(CakeDAO cakeRepository, CompositionDAO compositionRepository) {
        this.cakeRepository = cakeRepository;
        this.compositionRepository = compositionRepository;
    }

    @Override
    public Cakes getCakes(){
        Cakes cakes = new Cakes();
        cakes.setCakeList(getSomeCake(0, 1000));
        return cakes;
    }


    @Override
    public CakeDetail getCakeById(Long id) {
        CakeEntity cakeEntity = cakeRepository.getById(id);
        CakeDetail cakeDetail = new CakeDetail();
        cakeDetail.setName(cakeEntity.getName());
        cakeDetail.setCalories(cakeEntity.getCalories());
        cakeDetail.setDescription(cakeEntity.getDescription());
        cakeDetail.setImage(cakeEntity.getImage());
        cakeDetail.setPrice(cakeEntity.getPrice());
        cakeDetail.setWeight(cakeEntity.getWeight());
        cakeDetail.setId(cakeEntity.getId());
        Set<String> composition = cakeEntity.getComposition().stream().map(c -> c.getName()).collect(Collectors.toSet());
        cakeDetail.setComposition(composition);
        return cakeDetail;
    }

    @Override
    public List<Cake> getSomeCake(Integer page, Integer size) {
        Pageable limit = PageRequest.of(page, size);
        //List<CakeEntity> cakeEntities = cakeRepository.findAll(limit).toList();
        List<CakeEntity> cakeEntities = cakeRepository.findAll(page, size);

        return cakeEntities.stream().map(ce -> {
            Cake cake = new Cake();
            cake.setWeight(ce.getWeight());
            cake.setCalories(ce.getCalories());
            cake.setPrice(ce.getPrice());
            cake.setImage(ce.getImage());
            cake.setName(ce.getName());
            cake.setId(ce.getId());
            return cake;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateCake(Cake cake) {
        CakeEntity ce = cakeRepository.getById(cake.getId());
        ce.setCalories(cake.getCalories());
        ce.setImage(cake.getImage());
        ce.setPrice(cake.getPrice());
        ce.setWeight(cake.getWeight());
        ce.setName(cake.getName());
        cakeRepository.save(ce);
    }

    @Override
    public void updateOrCreateCakeDetail(CakeDetail cakeDetail) {
        CakeEntity ce;
        if(cakeDetail.getId() == null)
            ce = new CakeEntity();
        else
            ce = cakeRepository.getById(cakeDetail.getId());
        ce.setCalories(cakeDetail.getCalories());
        ce.setImage(cakeDetail.getImage());
        ce.setPrice(cakeDetail.getPrice());
        ce.setWeight(cakeDetail.getWeight());
        ce.setName(cakeDetail.getName());
        ce.setDescription(cakeDetail.getDescription());
        cakeDetail.getComposition().forEach(c -> {
            CompositionEntity composition = compositionRepository.findByName(c);
            if(composition == null){
                composition = new CompositionEntity();
                composition.setName(c);
                composition = compositionRepository.save(composition);
            }
            ce.getComposition().add(composition);
        });
        cakeRepository.save(ce);
    }

}
