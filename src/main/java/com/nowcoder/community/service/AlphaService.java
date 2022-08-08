package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author yanhao
 * @note
 * @create 2022-08-06 上午 11:34
 */
@Service
//@Scope("prototype") //多实例模式（一般都是单例）
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    //实例化AlphaService --> 初始化AlphaService -->销毁AlphaService
    public AlphaService() {
        System.out.println("实例化AlphaService");
    }
    @PostConstruct
    public void init() {
        System.out.println("初始化AlphaService");
    }
    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
