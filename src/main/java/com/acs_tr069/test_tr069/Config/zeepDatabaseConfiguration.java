package com.acs_tr069.test_tr069.Config;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.acs_tr069.test_tr069.zeep.repository",
    entityManagerFactoryRef = "zeepEntityManagerFactory",
    transactionManagerRef = "zeepTransactionManager"
)
public class zeepDatabaseConfiguration {

    @Autowired
    private Environment env;

    @Bean(name = "zeepDataSource")
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(env.getProperty("zeep.datasource.url"));
        ds.setUsername(env.getProperty("zeep.datasource.username"));
        ds.setPassword(env.getProperty("zeep.datasource.password"));
        ds.setDriverClassName(env.getProperty("zeep.datasource.driver-class-name"));
        return ds;
    }

    @Bean(name = "zeepEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource());
        JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        bean.setJpaVendorAdapter(adapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("zeep.jpa.hibernate.ddl-auto"));
        bean.setJpaPropertyMap(properties);
        bean.setPackagesToScan("com.acs_tr069.test_tr069.zeep.entity");
        return bean;
    }

    @Bean(name = "zeepTransactionManager")
    public PlatformTransactionManager zeepTransactionManager(
            @Qualifier("zeepEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
