package dpi.model.loan

import java.io.Serializable

/**
 *
 * This class stores all information about a
 * request that a client submits to get a loan.
 *
 */
data class LoanRequest(val ssn: Int, val amount: Int, val time: Int, val clientRef: String) : Serializable
