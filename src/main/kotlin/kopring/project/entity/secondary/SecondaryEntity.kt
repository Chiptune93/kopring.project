package kopring.project.entity.secondary

import jakarta.persistence.*

@Entity
@Table(name = "secondary_entity")
data class SecondaryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int = 0,

    val name: String = "",

    val age: Int = 0,

    val city: String = ""
)
