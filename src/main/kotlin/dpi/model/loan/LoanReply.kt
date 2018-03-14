package dpi.model.loan

import java.io.Serializable

/**
 *
 * This class stores all information about a bank offer
 * as a response to a client loan request.
 */
data class LoanReply(val interest: Double, val quoteID: String, val originalRequest: LoanRequest? = null) : Serializable
