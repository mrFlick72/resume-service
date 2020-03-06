package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph

object CellFactory {


    fun photoCell(image: ByteArray): Cell {
        val newCell = newCell()
        if (image.isNotEmpty()) {
            val createJpeg = ImageDataFactory.createJpeg(image)

            newCell.add(Image(createJpeg)
                    .scaleToFit(150f, 150f)
            )
        }

        return newCell
    }

    fun newSectionCell(content: String): Cell {
        return newCell(isFirstColumn = true, withRightBorder = true).add(Paragraph(content).setBold())
    }

    fun newFirstCell(content: String): Cell {
        return newCell(isFirstColumn = true, withRightBorder = true).add(Paragraph(content))
    }

    fun newSecondCell(content: String, bold: Boolean = false): Cell {
        val cell = newCell().add(Paragraph(content))
        return if(bold){
            cell.setBold()
        } else{
            cell
        }
    }

    fun newCell(withRightBorder: Boolean = false, isFirstColumn: Boolean = false): Cell {
        return Cell().setWidth(PageSize.A4.width * (if (isFirstColumn) 0.25f else 0.80f))
                .setBorder(Border.NO_BORDER).setFontSize(10f)
                .let {
                    if (withRightBorder) {
                        it.setBorderRight(SolidBorder(1f))
                    }
                    it
                }
    }
}