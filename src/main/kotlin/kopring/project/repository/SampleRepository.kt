package kopring.project.repository

import kopring.project.data.SampleEntity
import org.springframework.data.repository.CrudRepository

interface SampleRepository : CrudRepository<SampleEntity, Int>
