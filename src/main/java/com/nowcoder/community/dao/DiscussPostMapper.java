package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yanhao
 * @note
 * @create 2022-08-06 下午 9:24
 */
@Mapper
public interface DiscussPostMapper {

    //分页查询帖子
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit); //, int orderMode

    //查询帖子的行数(Param注解用于给起始参数取别名；如果只有一个参数，并且在<if>里面使用，测必须加别名）
    int selectDiscussPostRows(@Param("userId") int userId);

//    int insertDiscussPost(DiscussPost discussPost);
//
//    DiscussPost selectDiscussPostById(int id);
//
//    int updateCommentCount(int id, int commentCount);
//
//    int updateType(int id, int type);
//
//    int updateStatus(int id, int status);
//
//    int updateScore(int id, double score);


}
