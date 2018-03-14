package dpi.model.bank

import java.io.Serializable

/**
 * This class stores information about the nl.teunw.dpi.clients.bank reply
 * to a loan request of the specific client
 *
 */
data class BankInterestReply (val interest: Double = 0.toDouble(), val quoteId: String? = null, val bankInterestRequest: BankInterestRequest? = null) : Serializable