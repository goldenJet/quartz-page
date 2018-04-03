package com.education.repository;

import com.education.pojo.JobEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobEntityRepository extends PagingAndSortingRepository<JobEntity, Long>, JpaSpecificationExecutor {

    @Query(value = "select jb from JobEntity jb where jb.jobName = ?1 and jb.jobGroup = ?2 and jb.isDelete = false")
    JobEntity findByJobNameAndJobGroup(String jobName, String jobGroup);
}
