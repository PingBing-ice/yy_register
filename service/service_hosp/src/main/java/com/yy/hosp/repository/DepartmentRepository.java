package com.yy.hosp.repository;

import com.yy.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ice
 * @date 2022/8/3 12:32
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department findByHoscodeAndDepcode(String hoscode, String depcode);

    List<Department> findAllByHoscode(String hoscode);

}
