package kopring.project.entity.primary

import jakarta.persistence.*

@Entity
@Table(name = "primary_entity")
data class PrimaryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0,

    val name: String = "",

    val age: Int = 0,

    val city: String = ""

)
