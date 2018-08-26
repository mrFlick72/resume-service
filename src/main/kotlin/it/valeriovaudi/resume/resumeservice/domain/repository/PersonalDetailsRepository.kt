package it.valeriovaudi.resume.resumeservice.domain.repository

import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import org.reactivestreams.Publisher

interface PersonalDetailsRepository {

    fun findOne(resumeId: String): Publisher<PersonalDetails>

    fun findOneWithoutPhoto(resumeId: String): Publisher<PersonalDetails>

    fun save(resumeId: String, personalDetails: PersonalDetails): Publisher<PersonalDetails>

}