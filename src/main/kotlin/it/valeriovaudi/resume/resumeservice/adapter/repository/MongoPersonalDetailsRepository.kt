package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import it.valeriovaudi.resume.resumeservice.adapter.persistance.PersonalDetailsPersistanceModel
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.repository.PersonalDetailsRepository
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.time.Duration

class MongoPersonalDetailsRepository(private val mongoTemplate: ReactiveMongoTemplate,
                                     private val gridFsTemplate: GridFsTemplate) : PersonalDetailsRepository {

    override fun save(resumeId: String, personalDetails: Publisher<PersonalDetails>): Publisher<PersonalDetails> =

            personalDetails.toMono().flatMap {

                val personalDetails = it;
                val savedPersonalDetailsPersistanceModel =
                        mongoTemplate.save(PersonalDetailsPersistanceModel.fromDomainToPersistanceModel(resumeId, personalDetails))
                                .timeout(Duration.ofMinutes(1))


                val photoData =
                        if (personalDetails.photo.content.isNotEmpty())
                            Mono.fromCallable { gridFsTemplate.delete(Query.query(Criteria.where("metadata.resumeId").`is`(resumeId))) }
                                    .map {
                                        personalDetails.photo.content.inputStream().use {
                                            gridFsTemplate.store(it,
                                                    resumeId,
                                                    personalDetails.photo.fileExtension,
                                                    mutableMapOf("resumeId" to resumeId))
                                        }
                                    }
                        else Mono.empty()

                Mono.zip(savedPersonalDetailsPersistanceModel, photoData)
                        .map { personalDetails }
                        .onErrorReturn(PersonalDetails.emptyPersonalDetails())
            }


    override fun findOneWithoutPhoto(resumeId: String): Publisher<PersonalDetails> =
            mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(resumeId)),
                    PersonalDetailsPersistanceModel::class.java)
                    .map {
                        PersonalDetails(photo = PersonalDetailsPhoto.emptyPersonalDetailsPhoto(),
                                zip = it.zip, taxCode = it.taxCode,
                                state = it.state, sex = Sex.valueOf(it.sex),
                                region = it.region, mobile = it.mobile,
                                mail = it.mail, city = it.city,
                                birthDate = it.birthDate, address = it.address,
                                lastName = it.lastName, firstName = it.firstName)
                    }


    override fun findOne(resumeId: String): Publisher<PersonalDetails> =
            Mono.zip(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                    PersonalDetailsPersistanceModel::class.java),
                    Mono.fromCallable { gridFsTemplate.getResource(resumeId) })
                    .map {
                        val personalData = it.t1
                        val resource = it.t2

                        val photo = PersonalDetailsPhoto(content = resource.inputStream.readAllBytes(),
                                fileExtension = resource.contentType)

                        PersonalDetails(photo = photo,
                                zip = personalData.zip, taxCode = personalData.taxCode,
                                state = personalData.state, sex = Sex.valueOf(personalData.sex),
                                region = personalData.region, mobile = personalData.mobile,
                                mail = personalData.mail, city = personalData.city,
                                birthDate = personalData.birthDate, address = personalData.address,
                                lastName = personalData.lastName, firstName = personalData.firstName)
                    }
}