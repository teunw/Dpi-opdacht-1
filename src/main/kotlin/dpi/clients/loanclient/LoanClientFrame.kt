package dpi.clients.loanclient

import com.google.gson.Gson
import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import dpi.clients.loanbroker.LoanBrokerFrame
import dpi.model.loan.LoanRequest
import java.awt.EventQueue
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets

import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.border.EmptyBorder

import dpi.requestreply.RequestReply
import dpi.model.loan.LoanReply
import dpi.requestreply.serialize

class LoanClientFrame : JFrame() {
    private val contentPane: JPanel
    private val tfSSN: JTextField
    private val listModel = DefaultListModel<RequestReply<LoanRequest, LoanReply>>()
    private val requestReplyList: JList<RequestReply<LoanRequest, LoanReply>>

    private val tfAmount: JTextField
    private val lblNewLabel: JLabel
    private val lblNewLabel_1: JLabel
    private val tfTime: JTextField

    private val loanRequestChannel: Channel? = null
    private val loanReplyChannel: Channel? = null

    /**
     * Create the frame.
     */
    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = "localhost"
        val rabbitMq = connectionFactory.newConnection()

        title = "Loan Client"

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setBounds(100, 100, 684, 619)
        contentPane = JPanel()
        contentPane.border = EmptyBorder(5, 5, 5, 5)
        setContentPane(contentPane)
        val gbl_contentPane = GridBagLayout()
        gbl_contentPane.columnWidths = intArrayOf(0, 0, 30, 30, 30, 30, 0)
        gbl_contentPane.rowHeights = intArrayOf(30, 30, 30, 30, 30)
        gbl_contentPane.columnWeights = doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, java.lang.Double.MIN_VALUE)
        gbl_contentPane.rowWeights = doubleArrayOf(0.0, 0.0, 0.0, 0.0, java.lang.Double.MIN_VALUE)
        contentPane.layout = gbl_contentPane

        val lblBody = JLabel("ssn")
        val gbc_lblBody = GridBagConstraints()
        gbc_lblBody.insets = Insets(0, 0, 5, 5)
        gbc_lblBody.gridx = 0
        gbc_lblBody.gridy = 0
        contentPane.add(lblBody, gbc_lblBody)

        tfSSN = JTextField()
        val gbc_tfSSN = GridBagConstraints()
        gbc_tfSSN.fill = GridBagConstraints.HORIZONTAL
        gbc_tfSSN.insets = Insets(0, 0, 5, 5)
        gbc_tfSSN.gridx = 1
        gbc_tfSSN.gridy = 0
        contentPane.add(tfSSN, gbc_tfSSN)
        tfSSN.columns = 10

        lblNewLabel = JLabel("amount")
        val gbc_lblNewLabel = GridBagConstraints()
        gbc_lblNewLabel.insets = Insets(0, 0, 5, 5)
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST
        gbc_lblNewLabel.gridx = 0
        gbc_lblNewLabel.gridy = 1
        contentPane.add(lblNewLabel, gbc_lblNewLabel)

        tfAmount = JTextField()
        val gbc_tfAmount = GridBagConstraints()
        gbc_tfAmount.anchor = GridBagConstraints.NORTH
        gbc_tfAmount.insets = Insets(0, 0, 5, 5)
        gbc_tfAmount.fill = GridBagConstraints.HORIZONTAL
        gbc_tfAmount.gridx = 1
        gbc_tfAmount.gridy = 1
        contentPane.add(tfAmount, gbc_tfAmount)
        tfAmount.columns = 10

        lblNewLabel_1 = JLabel("time")
        val gbc_lblNewLabel_1 = GridBagConstraints()
        gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST
        gbc_lblNewLabel_1.insets = Insets(0, 0, 5, 5)
        gbc_lblNewLabel_1.gridx = 0
        gbc_lblNewLabel_1.gridy = 2
        contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1)

        tfTime = JTextField()
        val gbc_tfTime = GridBagConstraints()
        gbc_tfTime.insets = Insets(0, 0, 5, 5)
        gbc_tfTime.fill = GridBagConstraints.HORIZONTAL
        gbc_tfTime.gridx = 1
        gbc_tfTime.gridy = 2
        contentPane.add(tfTime, gbc_tfTime)
        tfTime.columns = 10

        val btnQueue = JButton("send loan request")
        btnQueue.addActionListener {
            val ssn = Integer.parseInt(tfSSN.text)
            val amount = Integer.parseInt(tfAmount.text)
            val time = Integer.parseInt(tfTime.text)

            val request = LoanRequest(ssn, amount, time)
            listModel.addElement(RequestReply(request, null))

        }
        val gbc_btnQueue = GridBagConstraints()
        gbc_btnQueue.insets = Insets(0, 0, 5, 5)
        gbc_btnQueue.gridx = 2
        gbc_btnQueue.gridy = 2
        contentPane.add(btnQueue, gbc_btnQueue)

        val scrollPane = JScrollPane()
        val gbc_scrollPane = GridBagConstraints()
        gbc_scrollPane.gridheight = 7
        gbc_scrollPane.gridwidth = 6
        gbc_scrollPane.fill = GridBagConstraints.BOTH
        gbc_scrollPane.gridx = 0
        gbc_scrollPane.gridy = 4
        contentPane.add(scrollPane, gbc_scrollPane)

        requestReplyList = JList<RequestReply<LoanRequest, LoanReply>>(listModel)
        scrollPane.setViewportView(requestReplyList)
    }

    /**
     * This method returns the RequestReply line that belongs to the request from requestReplyList (JList).
     * You can call this method when an reply arrives in order to add this reply to the right request in requestReplyList.
     * @param request
     * @return
     */
    private fun getRequestReply(request: LoanRequest): RequestReply<LoanRequest, LoanReply>? {

        for (i in 0 until listModel.size) {
            val rr = listModel.get(i)
            if (rr.request === request) {
                return rr
            }
        }

        return null
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
                    val frame = LoanClientFrame()
                    frame.isVisible = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
