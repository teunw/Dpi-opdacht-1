package dpi.model.bank

import java.io.Serializable

/**
 *
 * This class stores all information about an request from a nl.teunw.dpi.clients.bank to offer
 * a loan to a specific client.
 */
data class BankInterestRequest(val amount: Int = 0,val time: Int = 0, val bankInterestReply: BankInterestReply? = null) : Serializable
