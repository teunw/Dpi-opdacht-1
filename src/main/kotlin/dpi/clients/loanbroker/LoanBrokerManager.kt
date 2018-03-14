package dpi.clients.loanbroker

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import dpi.model.bank.BankInterestRequest
import dpi.model.loan.LoanReply
import dpi.model.loan.LoanRequest
import dpi.requestreply.RequestReply
import dpi.requestreply.deserialize

class LoanBrokerManager {

    private val loanRequestChannel: Channel
    private val loanReplyChannel: Channel

    private val bankRequestChannel: Channel
    private val bankReplyChannel: Channel


    init {
        val connectionFactory = ConnectionFactory()
        connectionFactory.host = "localhost"
        val rabbitMq = connectionFactory.newConnection()

        this.loanRequestChannel = rabbitMq.createChannel()
        this.loanRequestChannel.queueDeclare(LoanRequestChannel, false, false, false, null)

        this.loanReplyChannel = rabbitMq.createChannel()
        this.loanReplyChannel.queueDeclare(LoanReplyChannel, false, false, false, null)

        this.bankRequestChannel = rabbitMq.createChannel()
        this.bankRequestChannel.queueDeclare(BankRequestChannel, false, false, false, null)

        this.bankReplyChannel = rabbitMq.createChannel()
        this.bankReplyChannel.queueDeclare(BankResponseChannel, false, false, false, null)

        this.loanRequestChannel.basicConsume(
                LoanRequestChannel,
                DeliverCallback { _, message ->
                    val loanRequest = deserialize<LoanRequest>(message.body)
                    val bankInterestRequest = BankInterestRequest(loanRequest.amount, loanRequest.time)
                }, null, null)
    }

    companion object {
        const val LoanRequestChannel = "LoanRequestQueue"
        const val LoanReplyChannel = "LoanReplyQueue"
        const val BankRequestChannel = "BankRequestChannel"
        const val BankResponseChannel = "BankResponseChannel"
    }
}