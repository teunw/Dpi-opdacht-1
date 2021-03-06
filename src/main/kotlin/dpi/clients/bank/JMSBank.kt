package dpi.clients.bank

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import dpi.clients.BankRequestChannel
import dpi.clients.BankResponseChannel
import dpi.clients.loanbroker.LoanBrokerManager
import dpi.model.bank.BankInterestReply
import dpi.model.bank.BankInterestRequest
import dpi.model.deserialize
import dpi.model.serialize

class JMSBank {
    private val bankRequestChannel: Channel
    private val bankReplyChannel: Channel

    val requestListeners = mutableListOf<(r: BankInterestRequest) -> Unit>()

    /**
     * Create the frame.
     */
    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = "localhost"
        val rabbitMq = connectionFactory.newConnection()

        this.bankRequestChannel = rabbitMq.createChannel()
        this.bankRequestChannel.queueDeclare(BankRequestChannel, false, false, false, null)

        this.bankReplyChannel = rabbitMq.createChannel()
        this.bankReplyChannel.queueDeclare(BankResponseChannel, false, false, false, null)

        this.bankRequestChannel.basicConsume(
                BankRequestChannel, true,
                DeliverCallback { _, message ->
                    requestListeners.forEach { it(deserialize(message.body)) }
                }, null, null)
    }

    fun sendInterestResponse(res: BankInterestReply) {
        this.bankReplyChannel.basicPublish("", BankResponseChannel, null, res.serialize())
    }
}
