package it.valeriovaudi.resume.resumeservice.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object Clock {
    val dateFormatter = DateTimeFormatter.ISO_DATE

    fun fromStringToLocalDate(date : String?) =  Optional.ofNullable(date).map { LocalDate.from(dateFormatter.parse(it)) }.orElse(null)
    fun fromLocalDateToString(date : LocalDate?) =  Optional.ofNullable(date).map { dateFormatter.format(it) }.orElse(null)
}