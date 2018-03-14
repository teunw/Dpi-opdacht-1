package dpi.clients.bank

import com.google.gson.Gson
import com.rabbitmq.client.*
import dpi.clients.loanbroker.LoanBrokerFrame
import dpi.requestreply.RequestReply
import dpi.model.bank.BankInterestReply
import dpi.model.bank.BankInterestRequest
import dpi.model.loan.LoanReply

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

class JMSBankFrame : JFrame() {
    private val contentPane: JPanel
    private val tfReply: JTextField
    private val listModel = DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>>()

    private val loanRequestChannel: Channel
    private val loanReplyChannel: Channel
    private val gson = Gson()

    /**
     * Create the frame.
     */
    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = "localhost"
        val rabbitMq = connectionFactory.newConnection()

        this.loanRequestChannel = rabbitMq.createChannel()
        this.loanRequestChannel.queueDeclare(LoanBrokerFrame.LoanRequestChannel, false, false, false, null)

        this.loanReplyChannel = rabbitMq.createChannel()
        this.loanReplyChannel.queueDeclare(LoanBrokerFrame.LoanReplyChannel, false, false, false, null)

        title = "JMS Bank - ABN AMRO"
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
        gbc_scrollPane.gridwidth = 5
        gbc_scrollPane.insets = Insets(0, 0, 5, 5)
        gbc_scrollPane.fill = GridBagConstraints.BOTH
        gbc_scrollPane.gridx = 0
        gbc_scrollPane.gridy = 0
        contentPane.add(scrollPane, gbc_scrollPane)

        val list = JList(listModel)
        scrollPane.setViewportView(list)

        val lblNewLabel = JLabel("type reply")
        val gbc_lblNewLabel = GridBagConstraints()
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST
        gbc_lblNewLabel.insets = Insets(0, 0, 0, 5)
        gbc_lblNewLabel.gridx = 0
        gbc_lblNewLabel.gridy = 1
        contentPane.add(lblNewLabel, gbc_lblNewLabel)

        tfReply = JTextField()
        val gbc_tfReply = GridBagConstraints()
        gbc_tfReply.gridwidth = 2
        gbc_tfReply.insets = Insets(0, 0, 0, 5)
        gbc_tfReply.fill = GridBagConstraints.HORIZONTAL
        gbc_tfReply.gridx = 1
        gbc_tfReply.gridy = 1
        contentPane.add(tfReply, gbc_tfReply)
        tfReply.columns = 10

        val btnSendReply = JButton("send reply")
        btnSendReply.addActionListener {
            val rr = list.selectedValue
            val interest = java.lang.Double.parseDouble(tfReply.text)
            val reply = BankInterestReply(interest, "ABN AMRO")
            if (rr != null && reply != null) {
                rr.reply = reply
                list.repaint()
                // todo: sent JMS message with the reply to Loan Broker
                val s = gson.toJson(reply)
                this.loanReplyChannel.basicPublish("", LoanBrokerFrame.LoanReplyChannel, null, s.toByteArray())
                println(s)
            }
        }
        val gbc_btnSendReply = GridBagConstraints()
        gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST
        gbc_btnSendReply.gridx = 4
        gbc_btnSendReply.gridy = 1
        contentPane.add(btnSendReply, gbc_btnSendReply)

        val deliverCallback = DeliverCallback { _, message ->
            run {
                val msgAsJson = String(message.body)
                println(msgAsJson)
                val decoded = gson.fromJson(msgAsJson, LoanReply::class.java)
                tfReply.text += "\n$decoded"
            }
        }
        this.loanRequestChannel.basicConsume(LoanBrokerFrame.LoanRequestChannel, deliverCallback, null, null)
    }

    companion object {

        /**
         *
         */
        private val serialVersionUID = 1L

        /**
         * Launch the application.
         */
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
