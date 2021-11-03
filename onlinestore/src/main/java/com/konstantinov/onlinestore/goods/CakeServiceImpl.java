package com.konstantinov.onlinestore.goods;

import com.konstantinov.onlinestore.rest.dto.Cake;
import com.konstantinov.onlinestore.rest.dto.CakeDetail;
import com.konstantinov.onlinestore.rest.dto.Cakes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CakeServiceImpl implements CakeService {
    private final CakeRepository cakeRepository;
    private final CompositionRepository compositionRepository;

    @Autowired
    public CakeServiceImpl(CakeRepository cakeRepository, CompositionRepository compositionRepository) {
        this.cakeRepository = cakeRepository;
        this.compositionRepository = compositionRepository;
    }

    @Override
    public Cakes getCakes(){
        List<CakeEntity> cakeEntityList = cakeRepository.findAll();
        List<Cake> cakeList = cakeEntityList.stream().map(c -> {
            Cake cake = new Cake();
            cake.setId(c.getId());
            cake.setCalories(c.getCalories());
            cake.setName(c.getName());
            cake.setImage(c.getImage());
            cake.setPrice(c.getPrice());
            cake.setWeight(c.getWeight());
            return cake;
        }).collect(Collectors.toList());
        Cakes cakes = new Cakes();
        cakes.setCakeList(cakeList);
        return cakes;
    }

    @Override
    public void addCake(CakeDetail cake) {
        CakeEntity cakeEntity = new CakeEntity();
        cakeEntity.setCalories(cake.getCalories());
        cakeEntity.setName(cake.getName());
        cakeEntity.setImage(cake.getImage());
        cakeEntity.setWeight(cake.getWeight());
        cakeEntity.setPrice(cake.getPrice());
        cakeEntity.setDescription(cake.getDescription());
        Set<CompositionEntity> compositionEntities = cake.getComposition().stream().map(c -> {
            CompositionEntity comp = new CompositionEntity();
            CompositionEntity fromBd = compositionRepository.findByName(c);
            if(fromBd != null)
                comp = fromBd;
            else
                comp.setName(c);
            return compositionRepository.save(comp);
        }).collect(Collectors.toSet());
        cakeEntity.setComposition(compositionEntities);
        cakeRepository.save(cakeEntity);
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
        Set<String> composition = cakeEntity.getComposition().stream().map(c -> c.getName()).collect(Collectors.toSet());
        cakeDetail.setComposition(composition);
        return cakeDetail;
    }
}
