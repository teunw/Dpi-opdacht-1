package dpi.clients.bank

import dpi.model.bank.BankInterestReply
import dpi.model.bank.BankInterestRequest
import java.awt.EventQueue
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.border.EmptyBorder

class JMSBankFrame : JFrame() {
    private val contentPane: JPanel
    private val replyTextField: JTextField
    private val bankRequestList = DefaultListModel<BankInterestRequest>()

    private val bank = JMSBank()

    /**
     * Create the frame.
     */
    init {
        title = "JMS Bank - ABN AMRO"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setBounds(100, 100, 450, 300)
        contentPane = JPanel()
        contentPane.border = EmptyBorder(5, 5, 5, 5)
        setContentPane(contentPane)

        val gridBagLayout = GridBagLayout()
        gridBagLayout.columnWidths = intArrayOf(46, 31, 86, 30, 89, 0)
        gridBagLayout.rowHeights = intArrayOf(233, 23, 0)
        gridBagLayout.columnWeights = doubleArrayOf(1.0, 0.0, 1.0, 0.0, 0.0, java.lang.Double.MIN_VALUE)
        gridBagLayout.rowWeights = doubleArrayOf(1.0, 0.0, java.lang.Double.MIN_VALUE)
        contentPane.layout = gridBagLayout

        val scrollPane = JScrollPane()
        val scrollPaneConstraints = GridBagConstraints()
        scrollPaneConstraints.gridwidth = 5
        scrollPaneConstraints.insets = Insets(0, 0, 5, 5)
        scrollPaneConstraints.fill = GridBagConstraints.BOTH
        scrollPaneConstraints.gridx = 0
        scrollPaneConstraints.gridy = 0
        contentPane.add(scrollPane, scrollPaneConstraints)

        val list = JList(bankRequestList)
        scrollPane.setViewportView(list)

        /**
         * Reply label
         */
        val replyLabel = JLabel("type reply")
        val replyLabelConstraints = GridBagConstraints()
        replyLabelConstraints.anchor = GridBagConstraints.EAST
        replyLabelConstraints.insets = Insets(0, 0, 0, 5)
        replyLabelConstraints.gridx = 0
        replyLabelConstraints.gridy = 1
        contentPane.add(replyLabel, replyLabelConstraints)

        /**
         * Reply textfield
         */
        replyTextField = JTextField()
        val replyTextFieldConstraints = GridBagConstraints()
        replyTextFieldConstraints.gridwidth = 2
        replyTextFieldConstraints.insets = Insets(0, 0, 0, 5)
        replyTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL
        replyTextFieldConstraints.gridx = 1
        replyTextFieldConstraints.gridy = 1
        contentPane.add(replyTextField, replyTextFieldConstraints)
        replyTextField.columns = 10

        val sendReplyButton = JButton("send reply")
        sendReplyButton.addActionListener {
            val bankRequest = list.selectedValue
            if (bankRequest != null) {
                println("No value selected")
                val interest = replyTextField.text.toDoubleOrNull()
                if (interest == null) {
                    println("Could not parse double")
                } else {
                    val reply = BankInterestReply(interest, "ABN AMRO", bankRequest.brokenRef)
                    list.repaint()
                    this.bank.sendInterestResponse(reply)
                }
            } else {
                println("No request found")
            }
        }
        val sendReplyButtonConstraints = GridBagConstraints()
        sendReplyButtonConstraints.anchor = GridBagConstraints.NORTHWEST
        sendReplyButtonConstraints.gridx = 4
        sendReplyButtonConstraints.gridy = 1
        contentPane.add(sendReplyButton, sendReplyButtonConstraints)

        this.bank.requestListeners.add({ this.bankRequestList.addElement(it) })
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                try {
                    val frame = JMSBankFrame()
                    frame.isVisible = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}