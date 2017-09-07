package de.toto.crt.game.gui.javafx

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import org.controlsfx.control.PropertySheet
import org.controlsfx.control.PropertySheet.Item
import org.controlsfx.property.BeanPropertyUtils

object PropertySheet : Dialog<String>() {
    val sheet = PropertySheet()
    var board = ChessBoard()

    fun setChessBoard(b: ChessBoard) {
        board = b;
        sheet.items.removeAll()
        sheet.items.addAll(BeanPropertyUtils.getProperties(board))
    }

    init {
        dialogPane.buttonTypes.add(ButtonType("OK", ButtonBar.ButtonData.OK_DONE))
        dialogPane.content = sheet

    }


}