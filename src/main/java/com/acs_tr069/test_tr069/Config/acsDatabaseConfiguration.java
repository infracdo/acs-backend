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
    basePackages = "com.acs_tr069.test_tr069.Repo",
    entityManagerFactoryRef = "acsEntityManagerFactory",
    transactionManagerRef = "acsTransactionManager"
)
public class acsDatabaseConfiguration {

    @Autowired
    private Environment env;

    @Bean(name = "acsDataSource")
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(env.getProperty("acs.datasource.url"));
        ds.setUsername(env.getProperty("acs.datasource.username"));
        ds.setPassword(env.getProperty("acs.datasource.password"));
        ds.setDriverClassName(env.getProperty("acs.datasource.driver-class-name"));
        return ds;
    }

    @Bean(name = "acsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource());
        JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        bean.setJpaVendorAdapter(adapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("acs.jpa.hibernate.ddl-auto"));
        bean.setJpaPropertyMap(properties);
        bean.setPackagesToScan("com.acs_tr069.test_tr069.Entity");
        return bean;
    }

    @Bean(name = "acsTransactionManager")
    public PlatformTransactionManager acsTransactionManager(
            @Qualifier("acsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}
