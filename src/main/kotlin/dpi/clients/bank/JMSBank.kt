package dpi.clients.bank

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import dpi.clients.loanbroker.LoanBrokerManager
import dpi.model.bank.BankInterestReply
import dpi.model.deserialize
import dpi.model.serialize

class JMSBank {
    private val bankRequestChannel: Channel
    private val bankReplyChannel: Channel

    val requestListeners = mutableListOf<JMSBankListener>()

    /**
     * Create the frame.
     */
    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = "localhost"
        val rabbitMq = connectionFactory.newConnection()

        this.bankRequestChannel = rabbitMq.createChannel()
        this.bankRequestChannel.queueDeclare(LoanBrokerManager.BankRequestChannel, false, false, false, null)

        this.bankReplyChannel = rabbitMq.createChannel()
        this.bankReplyChannel.queueDeclare(LoanBrokerManager.BankResponseChannel, false, false, false, null)

        this.bankRequestChannel.basicConsume(
                LoanBrokerManager.BankRequestChannel, true,
                DeliverCallback { consumerTag, message ->
                    requestListeners.forEach { it.onInterestRequest(deserialize(message.body)) }
                }, null, null)
    }

    fun sendInterestResponse(res: BankInterestReply) {
        this.bankReplyChannel.basicPublish("", LoanBrokerManager.BankResponseChannel, null, res.serialize())
    }
}
