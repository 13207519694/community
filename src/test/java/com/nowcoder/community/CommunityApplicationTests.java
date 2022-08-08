package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //以CommunityApplication为配置类运行程序
//实现ApplicationContextAware接口，获取spring容器（需重写方法）
public class CommunityApplicationTests implements ApplicationContextAware {

	//成员变量记住这个spring容器
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {
	}

	@Override
	//ApplicationContext就是spring容器（BeanFactory）的子接口
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);

		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		alphaDao = applicationContext.getBean("alphaDaoHibernate",AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		//实例化AlphaService --> 初始化AlphaService --> com.nowcoder.community.service.AlphaService@15dd5ac2 -->销毁AlphaService
		System.out.println(alphaService);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	//前面测试的是容器装配获取的原理，实际项目中都是使用自动装配@Autowired完成
	@Autowired
	//不加@Qualifier注解，则按优先级自动装配com.nowcoder.community.dao.AlphaDaoMyBatisImpl@1d96d872实例
	@Qualifier("alphaDaoHibernate") //加@Qualifier注解，则按指定alphaDaoHibernate装配实例
	private AlphaDao alphaDao;
	@Autowired
	private AlphaService alphaService;
	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test //DI：依赖注入
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}
}
