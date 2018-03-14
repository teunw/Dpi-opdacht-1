package dpi.model.loan

import java.io.Serializable

/**
 *
 * This class stores all information about a bank offer
 * as a response to a client loan request.
 */
data class LoanReply(val interest: Double = 0.0, val quoteID: String? = null, val clientRef: String) : Serializable
