package it.valeriovaudi.resume.resumeservice.adapter.persistance

import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "skills")
data class SkillPersistanceModel(@Id var id: String? = null,
                                 var resumeId: String,
                                 var family: String,
                                 var skills : List<String>)


