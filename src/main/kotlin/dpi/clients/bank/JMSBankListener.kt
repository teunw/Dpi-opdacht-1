package dpi.clients.bank

import dpi.model.bank.BankInterestRequest

interface JMSBankListener {
    fun onInterestRequest(bankRequest: BankInterestRequest)
}