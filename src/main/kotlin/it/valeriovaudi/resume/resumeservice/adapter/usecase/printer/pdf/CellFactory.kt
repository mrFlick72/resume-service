package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph

object CellFactory {

    val ROW_COLOR: Color = DeviceRgb(253, 253, 248)

    fun photoCell(image: ByteArray): Cell {
        val newCell = newCell()
        if (image.isNotEmpty()) {
            newCell.add(Image(ImageDataFactory.createJpeg(image)).setHeight(50f).setWidth(50f))
        }

        return newCell
    }

    fun newSectionCell(content: String): Cell {
        return newCell(isFirstColumn = true, withRightBorder = true).add(Paragraph(content).setBold())
    }

    fun newFirstCell(content: String): Cell {
        return newCell(isFirstColumn = true, withRightBorder = true).add(Paragraph(content))
    }

    fun newSecondCell(content: String): Cell {
        return newCell().add(Paragraph(content))
    }

    fun newCell(color: Color = ROW_COLOR,
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