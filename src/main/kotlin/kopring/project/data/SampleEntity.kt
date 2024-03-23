package kopring.project.data

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("postgres_person")
data class SampleEntity(
    @Id
    var id: Int,
    var age: Int,
    var name: String,
    var city: String,
) {}
