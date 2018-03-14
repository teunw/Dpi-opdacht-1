package dpi.model

import dpi.model.bank.BankInterestRequest
import dpi.model.loan.LoanRequest

data class BankLoanConnector(val loanRef: LoanRequest, val bankRef: BankInterestRequest)