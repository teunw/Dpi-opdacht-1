package dpi.clients.loanbroker

import com.rabbitmq.client.Channel
import dpi.model.bank.BankInterestReply
import dpi.model.bank.BankInterestRequest
import java.awt.EventQueue
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets

import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

import dpi.model.loan.LoanRequest


class LoanBrokerFrame : JFrame() {
    private val contentPane: JPanel
    private val listModel = DefaultListModel<JListLine>()
    private val list: JList<JListLine>

    /**
     * Create the frame.
     */
    init {
        title = "Loan Broker"
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setBounds(100, 100, 450, 300)
        contentPane = JPanel()
        contentPane.border = EmptyBorder(5, 5, 5, 5)
        setContentPane(contentPane)
        val gbl_contentPane = GridBagLayout()
        gbl_contentPane.columnWidths = intArrayOf(46, 31, 86, 30, 89, 0)
        gbl_contentPane.rowHeights = intArrayOf(233, 23, 0)
        gbl_contentPane.columnWeights = doubleArrayOf(1.0, 0.0, 1.0, 0.0, 0.0, java.lang.Double.MIN_VALUE)
        gbl_contentPane.rowWeights = doubleArrayOf(1.0, 0.0, java.lang.Double.MIN_VALUE)
        contentPane.layout = gbl_contentPane

        val scrollPane = JScrollPane()
        val gbc_scrollPane = GridBagConstraints()
        gbc_scrollPane.gridwidth = 7
        gbc_scrollPane.insets = Insets(0, 0, 5, 5)
        gbc_scrollPane.fill = GridBagConstraints.BOTH
        gbc_scrollPane.gridx = 0
        gbc_scrollPane.gridy = 0
        contentPane.add(scrollPane, gbc_scrollPane)

        list = JList(listModel)
        scrollPane.setViewportView(list)
    }

    private fun getRequestReply(request: LoanRequest): JListLine? {

        for (i in 0 until listModel.size) {
            val rr = listModel.get(i)
            if (rr.loanRequest === request) {
                return rr
            }
        }

        return null
    }

    fun add(loanRequest: LoanRequest) {
        listModel.addElement(JListLine(loanRequest))
    }


    fun add(loanRequest: LoanRequest, bankRequest: BankInterestRequest?) {
        val rr = getRequestReply(loanRequest)
        if (rr != null && bankRequest != null) {
            rr.bankRequest = bankRequest
            list.repaint()
        }
    }

    fun add(loanRequest: LoanRequest, bankReply: BankInterestReply?) {
        val rr = getRequestReply(loanRequest)
        if (rr != null && bankReply != null) {
            rr.bankReply = bankReply
            list.repaint()
        }
    }

    companion object {

        /**
         *
         */
        private val serialVersionUID = 1L

        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                try {
                    val frame = LoanBrokerFrame()
                    frame.isVisible = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


}
