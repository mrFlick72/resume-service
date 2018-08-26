package it.valeriovaudi.resume.resumeservice.domain.model

import java.util.*

data class PersonalDetailsPhoto(val content: ByteArray,
                                val fileName: String,
                                val fileExtension: String) {

    companion object {
        fun emptyPersonalDetailsPhoto() = PersonalDetailsPhoto(ByteArray(0), "resume-photo", "jpeg")
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonalDetailsPhoto

        if (!Arrays.equals(content, other.content)) return false
        if (fileName != other.fileName) return false
        if (fileExtension != other.fileExtension) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(content)
        result = 31 * result + fileName.hashCode()
        result = 31 * result + fileExtension.hashCode()
        return result
    }
}


