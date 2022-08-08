package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author yanhao
 * @note
 * @create 2022-08-06 上午 11:23
 */
@Repository("alphaDaoHibernate") //数据库交互的组件用Repository，可以重命名Bean（小写）方便直接获取
public class AlphaDaoHibernateImpl implements AlphaDao {

    @Override
    public String select() {
        return "Hibernate";
    }

}
