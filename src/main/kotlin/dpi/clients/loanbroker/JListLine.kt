package dpi.clients.loanbroker

import dpi.model.bank.BankInterestReply
import dpi.model.bank.BankInterestRequest
import dpi.model.loan.LoanRequest

/**
 * This class represents one line in the JList in Loan Broker.
 * This class stores all objects that belong to one LoanRequest:
 * - LoanRequest,
 * - BankInterestRequest, and
 * - BankInterestReply.
 * Use objects of this class to add them to the JList.
 *
 * @author 884294
 */
internal class JListLine(loanRequest: LoanRequest) {

    var loanRequest: LoanRequest? = null
    var bankRequest: BankInterestRequest? = null
    var bankReply: BankInterestReply? = null

    init {
        this.loanRequest = loanRequest
    }

    override fun toString(): String {
        return loanRequest!!.toString() + " || " + if (bankReply != null) bankReply!!.toString() else "waiting for reply..."
    }

}
