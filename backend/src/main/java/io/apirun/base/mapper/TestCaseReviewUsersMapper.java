package io.apirun.base.mapper;

import io.apirun.base.domain.TestCaseReviewUsers;
import io.apirun.base.domain.TestCaseReviewUsersExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestCaseReviewUsersMapper {
    long countByExample(TestCaseReviewUsersExample example);

    int deleteByExample(TestCaseReviewUsersExample example);

    int insert(TestCaseReviewUsers record);

    int insertSelective(TestCaseReviewUsers record);

    List<TestCaseReviewUsers> selectByExample(TestCaseReviewUsersExample example);

    int updateByExampleSelective(@Param("record") TestCaseReviewUsers record, @Param("example") TestCaseReviewUsersExample example);

    int updateByExample(@Param("record") TestCaseReviewUsers record, @Param("example") TestCaseReviewUsersExample example);
}