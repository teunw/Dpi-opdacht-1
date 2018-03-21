package dpi.clients.loanbroker

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import dpi.clients.BankRequestChannel
import dpi.clients.BankResponseChannel
import dpi.clients.LoanReplyChannel
import dpi.clients.LoanRequestChannel
import dpi.model.BankLoanConnector
import dpi.model.bank.BankInterestReply
import dpi.model.bank.BankInterestRequest
import dpi.model.deserialize
import dpi.model.loan.LoanReply
import dpi.model.loan.LoanRequest
import dpi.model.serialize
import java.util.*

class LoanBrokerManager {

    private val loanRequestChannel: Channel
    private val loanReplyChannel: Channel

    private val bankRequestChannel: Channel
    private val bankReplyChannel: Channel

    private val loanRequests = mutableListOf<BankLoanConnector>()

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
                LoanRequestChannel, true,
                DeliverCallback { _, message ->

                    val loanRequest = deserialize<LoanRequest>(message.body)
                    val ref = UUID.randomUUID()
                    val bankInterestRequest = BankInterestRequest(loanRequest.amount, loanRequest.time, loanRequest.ssn, ref.toString())

                    this.loanRequests.add(BankLoanConnector(loanRequest, bankInterestRequest))
                    this.bankRequestChannel.basicPublish("", BankRequestChannel, null, bankInterestRequest.serialize())

                    println("Received - LoanRequest - $LoanRequestChannel - ${loanRequest.clientRef}")
                    println("Transmitted - BankRequest - $BankRequestChannel - $ref")
                }, null, null)

        this.bankReplyChannel.basicConsume(
                BankResponseChannel, true,
                DeliverCallback { _, message ->
                    val bankResponse = deserialize<BankInterestReply>(message.body)
                    val loanRequest = this.loanRequests.find { it.bankRef.brokenRef == bankResponse.brokerRef }
                    val loanReply = LoanReply(bankResponse.interest, bankResponse.quoteId, loanRequest?.loanRef?.clientRef!!)

                    this.loanReplyChannel.basicPublish("", LoanReplyChannel, null, loanReply.serialize())

                    println("Received - BankReply - $LoanReplyChannel - ${bankResponse.brokerRef}")
                    println("Transmitted - LoanReply - $LoanReplyChannel - ${loanRequest.loanRef}")
                }, null, null
        )
    }

    companion object {

        fun main(args: Array<String>) {
            LoanBrokerManager()
        }
    }
}