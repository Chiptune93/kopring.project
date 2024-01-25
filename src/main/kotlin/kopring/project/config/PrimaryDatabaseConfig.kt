package kopring.project.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*

@Configuration // 설정 클래스로 지정한다.
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class]) // 데이터베이스 자동 설정 기능을 이용하지 않는다. (커스텀 데이터 소스 구성)
// 자동 설정 기능을 키게 되면, 스프링은 기본 데이터 소스를 어떤 것으로 지정해야 될 지 모르기 때문에, 기능을 꺼야 한다.
// 기본 데이터 소스를 히카리를 사용해서 @Primary를 통해 올려도 이를 기본 데이터 소스로 인식하지 않는 듯 하다.
// 아마. 자동으로 올리는 기능으로 인하여 새로 데이터 소스가 생성되어 설정 값이 존재하지 않아 그러는 듯 하다.
@EnableJpaRepositories( // Jpa Repository 스캔 및 엔티티/트랜잭션 매니저를 지정하기 위한 설정이다.
    basePackages = ["kopring.project.repo.primary"],
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
class PrimaryDatabaseConfig(private val env: Environment) {

    /**
     * 커스텀 데이터 소스 설정 가져오기. (설정 방식은 application.yml 에서 하는 방식, 자바에서 직접 하는 방식 등이 있다.)
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.first")
    fun firstDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    /**
     * 데이터 소스 생성, 설정 프로퍼티를 가져와 hikariCP 데이터 소스를 생성한다.
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.first.configuration")
    fun firstDataSource(firstDataSourceProperties: DataSourceProperties): HikariDataSource {
        return firstDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    /**
     * SessionFactoryBean과 동일하게 DataSource와 Hibernate Property, Entity가 위치한 package를 지정
     * Hibernate 기반으로 동작을 지정하는 JpaVendor를 설정
     * Hibernate vendor과 JPA 간의 Adapter를 설정
     */
    @Bean
    @Primary
    fun primaryEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        // 데이터 소스 사용 지정
        em.dataSource = firstDataSource(firstDataSourceProperties())
        // 해당 엔티티 매니저가 관리할 패키지 지정
        em.setPackagesToScan(*arrayOf("kopring.project.entity.primary"))

        // JPA Adapter 지정
        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setShowSql(true)
        vendorAdapter.setGenerateDdl(true)
        em.jpaVendorAdapter = vendorAdapter
        // JPA Properties 설정.
        em.setJpaProperties(primaryJpaProperties())

        return em
    }

    /**
     * @Transactional이 포함된 메서드가 호출될 경우, PlatformTransactionManager를 사용하여 트랜잭션을 시작하고,
     * 정상 여부에 따라 Commit 또는 Rollback 한다.
     */
    @Bean
    @Primary
    fun primaryTransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = primaryEntityManagerFactory().getObject()
        return transactionManager
    }

    /**
     * primary database config 의 JPA 설정 값 로드
     */
    fun primaryJpaProperties(): Properties {
        val properties = Properties()
        properties.setProperty("hibernate.ddl-auto", env.getProperty("spring.datasource.first.jpa.hibernate.ddl-auto"))
        properties.setProperty("hibernate.dialect", env.getProperty("spring.datasource.first.jpa.hibernate.dialect"))
        properties.setProperty("hibernate.format_sql", env.getProperty("spring.datasource.first.jpa.hibernate.format_sql"))
        properties.setProperty("hibernate.show_sql", env.getProperty("spring.datasource.first.jpa.hibernate.show_sql"))
        return properties
    }

}
