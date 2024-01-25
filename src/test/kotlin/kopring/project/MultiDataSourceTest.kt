import kopring.project.repo.primary.PrimaryRepository
import kopring.project.repo.secondary.SecondaryRepository
import kopring.project.config.PrimaryDatabaseConfig
import kopring.project.config.SecondaryDatabaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


// 패키징 경로의 위치가 실제 설정 클래스 와 동일 하지 않아, 설정 파일을 로드 하도록 설정
@SpringBootTest(classes = [PrimaryDatabaseConfig::class, SecondaryDatabaseConfig::class])
/**
 * 설정한 데이터 소스가 정상적 으로 올라 오는 지 테스트 메소드
 * 1. Primary DB
 * 2. Secondary DB
 */
class MultiDataSourceTest {

    @Autowired
    lateinit var primaryRepository: PrimaryRepository

    @Autowired
    lateinit var secondaryRepository: SecondaryRepository

    @Test
    fun testPrimaryDataSource() {
        Assertions.assertNotNull(primaryRepository)
        // Add your primary data source tests here
        println(primaryRepository.findAll())
        Assertions.assertEquals(0, primaryRepository.count())
    }

    @Test
    fun testSecondaryDataSource() {
        Assertions.assertNotNull(secondaryRepository)
        // Add your secondary data source tests here
        println(secondaryRepository.findAll())
        Assertions.assertEquals(0, secondaryRepository.count())
    }
}
