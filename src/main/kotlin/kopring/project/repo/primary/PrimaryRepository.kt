package kopring.project.repo.primary
import kopring.project.entity.primary.PrimaryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// Primary DataSource Repository
@Repository
interface PrimaryRepository : JpaRepository<PrimaryEntity, Int> { }
