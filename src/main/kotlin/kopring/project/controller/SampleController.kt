package kopring.project.controller

import kopring.project.data.SampleEntity
import kopring.project.repository.SampleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    var sampleRepository: SampleRepository
) {

    @GetMapping("/test")
    fun testData(): List<SampleEntity> {
        return sampleRepository.findAll().toList()
    }

}
