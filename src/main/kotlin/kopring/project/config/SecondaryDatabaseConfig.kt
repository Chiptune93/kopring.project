package kopring.service.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*


@Configuration
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
@EnableJpaRepositories(
    basePackages = ["kopring.service.repo.secondary"],
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager"
)
class SecondaryDatabaseConfig(private val env: Environment) {

    /**
     * 커스텀 데이터 소스 설정 가져오기. (설정 방식은 application.yml 에서 하는 방식, 자바에서 직접 하는 방식 등이 있다.)
     */
    @Bean
    @ConfigurationProperties("spring.datasource.second")
    fun secondDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    /**
     * 데이터 소스 생성, 설정 프로퍼티를 가져와 hikariCP 데이터 소스를 생성한다.
     */
    @Bean
    @ConfigurationProperties("spring.datasource.second.configuration")
    fun secondDataSource(secondDataSourceProperties: DataSourceProperties): HikariDataSource {
        return secondDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    /**
     * SessionFactoryBean과 동일하게 DataSource와 Hibernate Property, Entity가 위치한 package를 지정
     * Hibernate 기반으로 동작을 지정하는 JpaVendor를 설정
     * Hibernate vendor과 JPA 간의 Adapter를 설정
     */
    @Bean
    fun secondaryEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = secondDataSource(secondDataSourceProperties())
        em.setPackagesToScan(*arrayOf("kopring.service.entity.secondary"))

        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setShowSql(true)
        vendorAdapter.setGenerateDdl(true)
        em.jpaVendorAdapter = vendorAdapter
        // JPA Properties 설정.
        em.setJpaProperties(secondaryJpaProperties())

        return em
    }

    @Bean
    fun secondaryTransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = secondaryEntityManagerFactory().getObject()
        return transactionManager
    }

    /**
     * secondary database config 의 JPA 설정 값 로드
     */
    fun secondaryJpaProperties(): Properties {
        val properties = Properties()
        properties.setProperty("hibernate.ddl-auto", env.getProperty("spring.datasource.second.jpa.hibernate.ddl-auto"))
        properties.setProperty("hibernate.dialect", env.getProperty("spring.datasource.second.jpa.hibernate.dialect"))
        properties.setProperty("hibernate.format_sql",env.getProperty("spring.datasource.second.jpa.hibernate.format_sql"))
        properties.setProperty("hibernate.show_sql", env.getProperty("spring.datasource.second.jpa.hibernate.show_sql"))
        properties.setProperty("hibernate.default_schema", env.getProperty("spring.datasource.second.jpa.hibernate.default_schema"))
        properties.setProperty("hibernate.physical_naming_strategy", env.getProperty("spring.datasource.second.jpa.hibernate.physical_naming_strategy"))
        return properties
    }


}
