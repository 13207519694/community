package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author yanhao
 * @note
 * @create 2022-08-06 上午 11:27
 */
@Repository
@Primary //实现同一DAO的不同实现类中优先装备
public class AlphaDaoMyBatisImpl implements AlphaDao{

    @Override
    public String select() {
        return "MyBatis";
    }

}
