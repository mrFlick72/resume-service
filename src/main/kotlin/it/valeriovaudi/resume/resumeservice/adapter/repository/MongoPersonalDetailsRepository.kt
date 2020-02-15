package it.valeriovaudi.resume.resumeservice.adapter.repository

import com.mongodb.client.result.UpdateResult
import it.valeriovaudi.resume.resumeservice.adapter.repository.mapper.PersonalDetailsMapper
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.repository.PersonalDetailsRepository
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest

class MongoPersonalDetailsRepository(private val mongoTemplate: ReactiveMongoTemplate,
                                     @Value("\${aws.s3.bucket}") private val awsBucket: String,
                                     private val s3Client: S3AsyncClient) : PersonalDetailsRepository {

    companion object {
        fun collectionName() = "personalDetails"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("resumeId").isEqualTo(resumeId))
    }

    override fun delete(resumeId: String) =
            Mono.zip(
                    mongoTemplate.remove(findOneQuery(resumeId), collectionName()),
                    deletePhoto(resumeId)
            ).flatMap { Mono.just(Unit) }

    override fun save(resumeId: String, personalDetails: PersonalDetails): Publisher<PersonalDetails> {
        val personalDetailsMono =
                mongoTemplate.upsert(findOneQuery(resumeId),
                        Update.fromDocument(PersonalDetailsMapper.fromDomainToDocument(resumeId, personalDetails)), collectionName())
                        .onErrorResume { println("Error at ${it}"); Mono.just(UpdateResult.unacknowledged()) }

        val photoData =
                if (personalDetails.photo.content.isNotEmpty())
                    personalDetails.photo.content.let {
                        loadPhoto(resumeId, it)
                    }
                else Mono.just("");

        return Mono.zip(personalDetailsMono, photoData)
                .map { personalDetails }
                .onErrorReturn(PersonalDetails.emptyPersonalDetails())
    }


    override fun findOneWithoutPhoto(resumeId: String): Publisher<PersonalDetails> =
            findResumeBy(resumeId)
                    .map { PersonalDetailsMapper.fromDocumentToDomain(document = it) }


    override fun findOne(resumeId: String): Publisher<PersonalDetails> =
            Mono.zip(findResumeBy(resumeId),
                    getPhoto(resumeId))
                    .map {
                        val personalData = it.t1
                        val resource = it.t2

                        val photo = if (resource.asByteArray().size != 0)
                            PersonalDetailsPhoto(content = resource.asByteArray(),
                                    fileExtension = resource.response().contentType())
                        else PersonalDetailsPhoto.emptyPersonalDetailsPhoto()

                        PersonalDetailsMapper.fromDocumentToDomain(document = personalData, photo = photo)
                    }

    private fun findResumeBy(resumeId: String) =
            mongoTemplate.findOne(findOneQuery(resumeId), Document::class.java, collectionName())
                    .switchIfEmpty(Mono.just(Document(mutableMapOf())))


    private fun deletePhoto(resumeId: String) =
            Mono.fromCompletionStage { s3Client.deleteObject(DeleteObjectRequest.builder().bucket(this.awsBucket).key(resumeId).build()) }

    private fun loadPhoto(resume: String, content: ByteArray) =
            Mono.fromCompletionStage {
                s3Client.putObject(
                        PutObjectRequest.builder().bucket(this.awsBucket).key(resume).build(),
                        AsyncRequestBody.fromBytes(content)
                )
            }

    private fun getPhoto(resume: String) =
            Mono.fromCompletionStage {
                s3Client.getObject(GetObjectRequest.builder()
                        .bucket(this.awsBucket)
                        .key(resume)
                        .build(),
                        AsyncResponseTransformer.toBytes())
            }.onErrorResume { Mono.just(ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), ByteArray(0))) }
}