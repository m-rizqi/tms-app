package com.rizqi.tms.utility

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.rizqi.tms.model.AppBluetoothDevice

interface PrinterUtility {
    fun setupPrinter(appBluetoothDevice: AppBluetoothDevice) : EscPosPrinter
    fun print(printer: EscPosPrinter, formattedText : String)
}

class BluetoothPrinterUtility : PrinterUtility{
    override fun setupPrinter(appBluetoothDevice: AppBluetoothDevice): EscPosPrinter {
        return EscPosPrinter(
            BluetoothConnection(appBluetoothDevice.bluetoothDevice),
            203, appBluetoothDevice.width, appBluetoothDevice.charPerLine
        )
    }

    override fun print(printer: EscPosPrinter, formattedText: String) {
        printer.printFormattedText(formattedText)
    }
}