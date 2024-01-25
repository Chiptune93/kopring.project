package kopring.project.repo.secondary

import kopring.project.entity.secondary.SecondaryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// Secondary DataSource Repository
@Repository
interface SecondaryRepository : JpaRepository<SecondaryEntity, Int>
