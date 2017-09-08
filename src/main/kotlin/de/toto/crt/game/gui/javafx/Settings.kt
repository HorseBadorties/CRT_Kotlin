package de.toto.crt.game.gui.javafx

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.stage.Window
import org.controlsfx.control.PropertySheet
import org.controlsfx.property.BeanPropertyUtils

class SettingsDialog(parent: Window?, vararg beans: Any) : Dialog<String>() {

    init {
        initOwner(parent)
        isResizable = true
        title = "CRT - Settings"
        dialogPane.headerText = "Header is here"
        val sheet = PropertySheet()
        sheet.isModeSwitcherVisible = false
        sheet.isSearchBoxVisible = false
        beans.forEach {
            sheet.items.addAll(BeanPropertyUtils.getProperties(it) {
                it.propertyType != ChessBoard::class.java
            })
        }
        dialogPane.buttonTypes.add(ButtonType("OK", ButtonBar.ButtonData.OK_DONE))
        dialogPane.content = sheet

    }

}

class BoardProperties(val board: ChessBoard) {
    var whitePerspective: Boolean = board.isOrientationWhite
        set(value) { board.isOrientationWhite = value }

    var showGraphicsComments: Boolean = board.isShowingGraphicsComments
        set(value) { board.isShowingGraphicsComments = value }

    var showBoard: Boolean = board.isShowingBoard
        set(value) { board.isShowingBoard = value }

    var showPieces: Boolean = board.isShowingPieces
        set(value) { board.isShowingPieces = value }

    var showCoordinates: Boolean = board.isShowingSquareCoordinates
        set(value) { board.isShowingSquareCoordinates = value }
}

class FooProperties {
    var whatEverProperty: Int = 42
        set(value) {  }
}