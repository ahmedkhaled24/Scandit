package com.momentum.scandit.data

import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.barcode.data.Symbology
import java.io.Serializable
import java.util.*

class ScanResult(barcode: Barcode) : Serializable {
    val symbology: Symbology
    val data: String
    override fun equals(obj: Any?): Boolean {
        return hashCode() == obj.hashCode()
    }

    override fun hashCode(): Int {
        return Objects.hash(symbology, data)
    }

    init {
        symbology = barcode.symbology
        data = if (barcode.data != null) barcode.data!! else ""
    }
}