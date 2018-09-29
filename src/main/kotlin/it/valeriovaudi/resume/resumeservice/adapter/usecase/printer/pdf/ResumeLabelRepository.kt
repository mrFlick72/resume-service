package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import it.valeriovaudi.resume.resumeservice.domain.model.EducationType
import java.time.LocalDate

object ResumeLabelRepository {

    fun resumeDefaultLabel() =
            mapOf("firstName" to "First Name",
                    "lastName" to "Last Name",
                    "address" to "Address",
                    "zip" to "Zip",
                    "city" to "City",
                    "region" to "Region",
                    "mail" to "Mail",
                    "mobile" to "Mobile",
                    "birthDate" to "Birth Date",
                    "country" to "Country",
                    "sex" to "Sex",
                    "taxCode" to "Tax Code",
                    "company" to "Company",
                    "title" to "Title",
                    "type" to "Type",
                    "dateFrom" to "Starting Date",
                    "dateTo" to "Ending Date")

}