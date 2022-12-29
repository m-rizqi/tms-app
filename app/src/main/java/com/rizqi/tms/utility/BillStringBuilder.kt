package com.rizqi.tms.utility

import android.content.Context
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.textparser.PrinterTextParser
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.rizqi.tms.R
import com.rizqi.tms.model.BillItem
import com.rizqi.tms.model.TransactionWithItemInCashier

data class BillStringBuilder(
    val context: Context,
    val merchantImageItem : BillItem?,
    val merchantNameItem : BillItem?,
    val merchantAddressItem : BillItem?,
    val merchantCashierItem : BillItem?,
    val dateItem : BillItem?,
    val idTransactionItem : BillItem?,
    val charPerLine : Int,
    val blankLine : Int,
    val escPosPrinter: EscPosPrinter,
    val transactionWithItemInCashier: TransactionWithItemInCashier
) {
    private val imageSection = "[C]<img>%s</img>"
    private val merchantNameSection = "[L]%s"
    private val merchantAddressSection = "[L]%s"
    private val cashierSection = "[L]${context.getString(R.string.cashier)} : %s"
    private val dateSection = "[L]${context.getString(R.string.date)} %s"
    private val idSection = "[L]${context.getString(R.string.transaction_id)} : %s"
    private val headerItemSection = "[L]${context.getString(R.string.item)}[C]${context.getString(R.string.qty)}[R]${context.getString(R.string.total)}"
    private val mainItemSection = "[L]%s[C]%s[R]%s"
    private val pricePerItemSection = "[L]%s"
    private val totalSection = "[L]${context.getString(R.string.total)}[C][R]%s"

    fun build(): String {
        val builder = StringBuilder()
        if (merchantImageItem != null && merchantImageItem.isVisible && merchantImageItem.bitmapData != null){
            builder.append(String.format(
                imageSection, PrinterTextParserImg.bitmapToHexadecimalString(escPosPrinter, merchantImageItem.bitmapData?.resize(merchantImageItem.bitmapData!!.width / 2, merchantImageItem.bitmapData!!.height / 2))
            ))
            builder.append("\n")
        }
        builder.append("\n\n")
        if (merchantNameItem != null && merchantNameItem.isVisible){
            builder.append(String.format(
                merchantNameSection, merchantNameItem.textData
            ))
            builder.append("\n")
        }
        if (merchantAddressItem != null && merchantAddressItem.isVisible){
            builder.append(String.format(
                merchantAddressSection, merchantAddressItem.textData
            ))
            builder.append("\n")
        }
        builder.append(
            "-".repeat(charPerLine)
        )
        builder.append("\n")
        if (merchantCashierItem != null && merchantCashierItem.isVisible){
            builder.append(String.format(
                cashierSection, merchantCashierItem.textData
            ))
            builder.append("\n")
        }
        if (dateItem != null && dateItem.isVisible){
            builder.append(String.format(
                dateSection, getFormattedDateString(transactionWithItemInCashier.transaction.time, EEE_DD_MMM_YYYY_HH_MM)
            ))
            builder.append("\n")
        }
        if (idTransactionItem != null && idTransactionItem.isVisible){
            builder.append(String.format(
                idSection, transactionWithItemInCashier.transaction.id
            ))
            builder.append("\n")
        }
        builder.append(
            "-".repeat(charPerLine)
        )
        builder.append("\n")
        builder.append(headerItemSection)
        builder.append("\n")
        transactionWithItemInCashier.itemInCashiers.forEach {
            builder.append(String.format(
                mainItemSection, it.itemName, "${formatQuantity(it.quantity)} ${it.unitName}", context.getString(R.string.rp_no_comma, ThousandFormatter.format(it.total))
            ))
//            builder.append("\n")
//            builder.append(String.format(
//                pricePerItemSection, "${context.getString(R.string.rp_no_comma, ThousandFormatter.format(it.pricePerItem))}/${it.unitName}"
//            ))
            builder.append("\n\n")
        }
        builder.append(
            "-".repeat(charPerLine)
        )
        builder.append("\n")
        builder.append(String.format(
            totalSection, context.getString(R.string.rp_no_comma, ThousandFormatter.format(transactionWithItemInCashier.transaction.total))
        ))
        builder.append(
            "\n".repeat(blankLine)
        )
        return builder.toString()
    }
}