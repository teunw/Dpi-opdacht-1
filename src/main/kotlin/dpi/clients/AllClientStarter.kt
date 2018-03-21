package dpi.clients

import dpi.clients.bank.JMSBankFrame
import dpi.clients.loanbroker.LoanBrokerManager
import dpi.clients.loanclient.LoanClientFrame

fun main(s: Array<String>) {
    LoanClientFrame.main(s)
    JMSBankFrame.main(s)
    LoanBrokerManager.main(s)
}