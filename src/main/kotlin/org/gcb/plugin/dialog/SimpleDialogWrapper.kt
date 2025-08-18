package org.gcb.plugin.dialog

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class SimpleDialogWrapper {
    protected fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        val label = JLabel("Testing")
        label.preferredSize = Dimension(100, 100)
        dialogPanel.add(label, BorderLayout.CENTER)

        return dialogPanel
    }
}