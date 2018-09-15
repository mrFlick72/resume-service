package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoResumeRepository
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import it.valeriovaudi.resume.resumeservice.domain.usecase.ResumePrinter
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.beans.Introspector
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class PdfResumePrinter(private val resumeRepository: MongoResumeRepository) : ResumePrinter {
    val ROW_COLOR: Color = DeviceRgb(253, 253, 248)

    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val resumeDefaultLabel = mapOf("firstName" to "First Name",
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
            "taxCode" to "Tax Code")

    override fun printResumeFor(resumeId: String): InputStream {
        val pdfPath = Files.createTempFile(UUID.randomUUID().toString(), ".pdf")

        Files.newOutputStream(pdfPath).use { pdfStream ->
            PdfWriter(pdfStream).use { pdfWriter ->
                PdfDocument(pdfWriter).use { pdfDocument ->
                    val document = Document(pdfDocument)
                    this.makePdf(resumeId, document)
                    document.close()
                }
            }

            return FileInputStream(pdfPath.toFile())
        }
    }

    fun makePdf(resumeId: String, document: Document): Unit? {
        return this.resumeRepository.findOne(resumeId).toMono()
                .flatMap {
                    document.setFontSize(14f)

                    val table = Table(2).setWidth(PageSize.A4.width * 0.80f).setAutoLayout()
                    table.setPaddingRight(25f)
                    table.setMarginRight(25f)

                    newPersonalDetailsCells(table, it.personalDetails)
                    document.add(table)

                    Mono.just(Unit)
                }.block()
    }


    fun newPersonalDetailsCells(table: Table, personalDetails: PersonalDetails) {
        table.addCell(newFirstCell("Resume")).addCell(photoCell(personalDetails.photo.content))
        table.addCell(newFirstCell("Personal Details")).addCell(newSecondCell(""))

        Introspector.getBeanInfo(personalDetails::class.java).propertyDescriptors.map {
            val data = it.readMethod.invoke(personalDetails)
            val label = resumeDefaultLabel.getOrDefault(it.name, "")

            when (data) {
                is String -> table.addCell(newFirstCell(label)).addCell(newSecondCell(data))
                is LocalDate -> table.addCell(newFirstCell(label)).addCell(newSecondCell(dateTimeFormatter.format(data)))
                else -> {
                }
            }

        }
    }

    private fun photoCell(image: ByteArray): Cell {
        val newCell = newCell()
        if (image.isNotEmpty()) {
            newCell.add(Image(ImageDataFactory.createJpeg(image)).setHeight(50f).setWidth(50f))
        }

        return newCell
    }

    private fun newFirstCell(content: String): Cell {
        return newCell(isFirstColumn = true, withRightBorder = true).add(Paragraph(content))
    }

    private fun newSecondCell(content: String): Cell {
        return newCell().add(Paragraph(content))
    }

    private fun newCell(color: Color = ROW_COLOR,
                        withRightBorder: Boolean = false, isFirstColumn: Boolean = false): Cell {
        return Cell().setWidth(PageSize.A4.width * (if (isFirstColumn) 0.25f else 0.80f))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(color).setFontSize(10f)
                .let {
                    if (withRightBorder) {
                        it.setBorderRight(SolidBorder(1f))
                    }
                    it
                }
    }
}