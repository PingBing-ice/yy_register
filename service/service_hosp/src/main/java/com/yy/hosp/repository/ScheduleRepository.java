package com.yy.hosp.repository;

import com.yy.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author ice
 * @date 2022/8/4 12:44
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    Schedule findByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findAllByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date dateTime);
}
