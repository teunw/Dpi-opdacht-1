package dpi.clients.loanclient

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import dpi.clients.loanbroker.LoanBrokerManager
import dpi.model.deserialize
import dpi.model.loan.LoanReply
import dpi.model.loan.LoanRequest
import dpi.model.serialize
import java.awt.EventQueue
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class LoanClientFrame : JFrame() {
    private val contentPane: JPanel
    private val ssnTextField: JTextField
    private val listModel = DefaultListModel<LoanReply>()
    private val requestReplyList: JList<LoanReply>

    private val amountTextField: JTextField
    private val amountLabel: JLabel
    private val timeLabel: JLabel
    private val timeTextField: JTextField

    private val loanRequestChannel: Channel
    private val loanReplyChannel: Channel

    private val ownRefs = mutableMapOf<String, LoanRequest>()

    /**
     * Create the frame.
     */
    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = "localhost"
        val rabbitMq = connectionFactory.newConnection()

        this.loanRequestChannel = rabbitMq.createChannel()
        this.loanRequestChannel.queueDeclare(LoanBrokerManager.LoanRequestChannel, false, false, false, null)

        this.loanReplyChannel = rabbitMq.createChannel()
        this.loanReplyChannel.queueDeclare(LoanBrokerManager.LoanReplyChannel, false, false, false, null)

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

        val ssnLabel = JLabel("ssn")
        val sslLAbelConstraints = GridBagConstraints()
        sslLAbelConstraints.insets = Insets(0, 0, 5, 5)
        sslLAbelConstraints.gridx = 0
        sslLAbelConstraints.gridy = 0
        contentPane.add(ssnLabel, sslLAbelConstraints)

        ssnTextField = JTextField()
        val ssnTextFieldConstraints = GridBagConstraints()
        ssnTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL
        ssnTextFieldConstraints.insets = Insets(0, 0, 5, 5)
        ssnTextFieldConstraints.gridx = 1
        ssnTextFieldConstraints.gridy = 0
        contentPane.add(ssnTextField, ssnTextFieldConstraints)
        ssnTextField.columns = 10

        amountLabel = JLabel("amount")
        val amountLabelConstraints = GridBagConstraints()
        amountLabelConstraints.insets = Insets(0, 0, 5, 5)
        amountLabelConstraints.anchor = GridBagConstraints.WEST
        amountLabelConstraints.gridx = 0
        amountLabelConstraints.gridy = 1
        contentPane.add(amountLabel, amountLabelConstraints)

        amountTextField = JTextField()
        val amountTextFieldConstraints = GridBagConstraints()
        amountTextFieldConstraints.anchor = GridBagConstraints.NORTH
        amountTextFieldConstraints.insets = Insets(0, 0, 5, 5)
        amountTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL
        amountTextFieldConstraints.gridx = 1
        amountTextFieldConstraints.gridy = 1
        contentPane.add(amountTextField, amountTextFieldConstraints)
        amountTextField.columns = 10

        timeLabel = JLabel("time")
        val timeLabelConstraints = GridBagConstraints()
        timeLabelConstraints.anchor = GridBagConstraints.EAST
        timeLabelConstraints.insets = Insets(0, 0, 5, 5)
        timeLabelConstraints.gridx = 0
        timeLabelConstraints.gridy = 2
        contentPane.add(timeLabel, timeLabelConstraints)

        timeTextField = JTextField()
        val timeTextFieldConstraints = GridBagConstraints()
        timeTextFieldConstraints.insets = Insets(0, 0, 5, 5)
        timeTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL
        timeTextFieldConstraints.gridx = 1
        timeTextFieldConstraints.gridy = 2
        contentPane.add(timeTextField, timeTextFieldConstraints)
        timeTextField.columns = 10

        val queueBtn = JButton("send loan request")
        queueBtn.addActionListener {
            val ssn = Integer.parseInt(ssnTextField.text)
            val amount = Integer.parseInt(amountTextField.text)
            val time = Integer.parseInt(timeTextField.text)

            val uuid = UUID.randomUUID()
            val request = LoanRequest(ssn, amount, time, uuid.toString())
            this.ownRefs[uuid.toString()] = request

            this.loanRequestChannel.basicPublish("", LoanBrokerManager.LoanRequestChannel, null, request.serialize())
        }
        val queueBtnConstraints = GridBagConstraints()
        queueBtnConstraints.insets = Insets(0, 0, 5, 5)
        queueBtnConstraints.gridx = 2
        queueBtnConstraints.gridy = 2
        contentPane.add(queueBtn, queueBtnConstraints)

        val scrollPane = JScrollPane()
        val gbc_scrollPane = GridBagConstraints()
        gbc_scrollPane.gridheight = 7
        gbc_scrollPane.gridwidth = 6
        gbc_scrollPane.fill = GridBagConstraints.BOTH
        gbc_scrollPane.gridx = 0
        gbc_scrollPane.gridy = 4
        contentPane.add(scrollPane, gbc_scrollPane)

        requestReplyList = JList<LoanReply>(listModel)
        scrollPane.setViewportView(requestReplyList)

        this.loanReplyChannel.basicConsume(
                LoanBrokerManager.LoanReplyChannel, true,
                DeliverCallback { _, message -> listModel.addElement(deserialize<LoanReply>(message.body)) }
                , null, null)
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
