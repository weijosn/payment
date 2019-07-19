package com.cdzg.money;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * money_service数据库配置对象
 * @version 1.0.0
 */
@Configuration
@MapperScan(basePackages = { "com.cdzg.money.mapper" }, sqlSessionFactoryRef = "sqlSessionFactoryForMoney")
public class MybatisConfig {


    @Bean(name = "paymentDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.payment")
    @Order(1)
    public DataSource getDateSource1() {
        return DataSourceBuilder.create().build();
    }


	/**
	 * 构建sqlSessionFactory
	 * 
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Primary
	@Order(2)
	public SqlSessionFactory sqlSessionFactoryForMoney(@Qualifier("paymentDataSource")DataSource dataSource ) throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		factoryBean.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));
		factoryBean.setTypeAliasesPackage("com.cdzg.money.mapper.entity");
		factoryBean.setDataSource(dataSource);
		SqlSessionFactory sqlSessionFactory = factoryBean.getObject();
		org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
		configuration.setMapUnderscoreToCamelCase(true);
		return sqlSessionFactory;
	}

	@Bean
	@Primary
	@Order(3)
	public SqlSessionTemplate sqlSessionTemplateForMoney(@Autowired SqlSessionFactory sqlSessionFactory) throws Exception {
	    return new SqlSessionTemplate(sqlSessionFactory);
	}
}
