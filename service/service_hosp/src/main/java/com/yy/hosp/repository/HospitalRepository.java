package com.yy.hosp.repository;

import com.yy.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ice
 * @date 2022/8/3 12:32
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    Hospital findByHoscode(String hoscode);

    Hospital getByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String hosname);
}
