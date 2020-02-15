package it.valeriovaudi.resume.resumeservice.adapter.repository

import com.mongodb.client.result.UpdateResult
import it.valeriovaudi.resume.resumeservice.adapter.repository.mapper.PersonalDetailsMapper
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto.Companion.emptyPersonalDetailsPhoto
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
import reactor.util.function.Tuple2
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*

class MongoPersonalDetailsRepository(private val mongoTemplate: ReactiveMongoTemplate,
                                     @Value("\${aws.s3.bucket}") private val awsBucket: String,
                                     private val s3Client: S3AsyncClient) : PersonalDetailsRepository {

    companion object {
        fun collectionName() = "personalDetails"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("resumeId").isEqualTo(resumeId))
    }

    private var photoRepository: S3PhotoRepository = S3PhotoRepository(awsBucket, s3Client)

    override fun delete(resumeId: String) =
            Mono.zip(
                    mongoTemplate.remove(findOneQuery(resumeId), collectionName()),
                    photoRepository.deletePhoto(resumeId)
            ).flatMap { Mono.just(Unit) }

    override fun save(resumeId: String, personalDetails: PersonalDetails): Publisher<PersonalDetails> {
        val personalDetailsMono = storePersonaDetails(resumeId, personalDetails)

        val photoData = photoRepository.loadPhoto(resumeId, personalDetails.photo)

        return Mono.zip(personalDetailsMono, photoData)
                .map { personalDetails }
                .onErrorReturn(PersonalDetails.emptyPersonalDetails())
    }

    private fun storePersonaDetails(resumeId: String, personalDetails: PersonalDetails): Mono<UpdateResult> {
        return mongoTemplate.upsert(findOneQuery(resumeId),
                Update.fromDocument(PersonalDetailsMapper.fromDomainToDocument(resumeId, personalDetails)), collectionName())
                .onErrorResume { println("Error at ${it}"); Mono.just(UpdateResult.unacknowledged()) }
    }

    override fun findOneWithoutPhoto(resumeId: String): Publisher<PersonalDetails> =
            findResumeBy(resumeId)
                    .map { PersonalDetailsMapper.fromDocumentToDomain(document = it) }


    override fun findOne(resumeId: String): Publisher<PersonalDetails> =
            Mono.zip(findResumeBy(resumeId),
                    photoRepository.getPhoto(resumeId))
                    .map { makePersonalDetails(it) }

    private fun makePersonalDetails(it: Tuple2<Document, PersonalDetailsPhoto>): PersonalDetails =
            PersonalDetailsMapper.fromDocumentToDomain(document = it.t1, photo = it.t2)

    private fun findResumeBy(resumeId: String) =
            mongoTemplate.findOne(findOneQuery(resumeId), Document::class.java, collectionName())
                    .switchIfEmpty(Mono.just(Document(mutableMapOf())))

}

internal class S3PhotoRepository(@Value("\${aws.s3.bucket}") private val awsBucket: String,
                                 private val s3Client: S3AsyncClient) {

    companion object {
        fun personalDetailsBucketFolder() = "resume/personalDetails"
    }

    fun deletePhoto(resumeId: String) =
            Mono.fromCompletionStage {
                s3Client.deleteObject(DeleteObjectRequest
                        .builder()
                        .bucket(this.awsBucket)
                        .key("${personalDetailsBucketFolder()}/$resumeId")
                        .build())
            }

    fun loadPhoto(resumeId: String, photo: PersonalDetailsPhoto) =
            if (photo.content.isNotEmpty())
                photo.content.let {
                    Mono.fromCompletionStage {
                        s3Client.putObject(
                                PutObjectRequest.builder()
                                        .bucket(this.awsBucket)
                                        .key("${personalDetailsBucketFolder()}/$resumeId")
                                        .build(),
                                AsyncRequestBody.fromBytes(it)
                        )
                    }
                }
            else
                Mono.just(PutObjectResponse.builder().build())

    fun getPhoto(resumeId: String) =
            Mono.fromCompletionStage {
                s3Client.getObject(GetObjectRequest.builder()
                        .bucket(this.awsBucket)
                        .key("${personalDetailsBucketFolder()}/$resumeId")
                        .build(),
                        AsyncResponseTransformer.toBytes())
            }.onErrorResume { Mono.just(ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), ByteArray(0))) }
                    .map {
                        if (it.asByteArray().size != 0)
                            PersonalDetailsPhoto(content = it.asByteArray(), fileExtension = it.response().contentType())
                        else emptyPersonalDetailsPhoto()
                    }
}