package kopring.project.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
// 데이터베이스 자동 설정 기능을 이용하지 않는다. (커스텀 데이터 소스 구성)
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
// CrudRepository 를 확장하는 클래스를 찾아 JDBC Repository Beans으로 등록한다.
@EnableJdbcRepositories(basePackages = ["kopring.project.repository"])
class DatabaseConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.postgres")
    fun postgresDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgres.hikari")
    fun postgresDataSource(postgresDataSourceProperties: DataSourceProperties): HikariDataSource {
        return postgresDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    @Bean
    @Qualifier("transactionManager")
    @Primary
    fun postgresTransactionManager(postgresDataSource: DataSource?): DataSourceTransactionManager {
        return DataSourceTransactionManager(postgresDataSource!!)
    }

}
