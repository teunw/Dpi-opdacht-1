package dpi.requestreply

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * This class stores pairs Request-Reply. We will use this class in Loan Client and ABN Amro applications
 * in order to make it easier for us to store pairs Request-Reply as items in a GUI JList.
 * For example, in Loan Client application it will be RequestReply<LoanRequest></LoanRequest>,LoanReply>, and
 * in ABN Amro application it will be RequestReply<BankInterestRequest></BankInterestRequest>, BankInterestReply>.
 * @author 884294
 *
 * @param <REQUEST>
 * @param <REPLY>
</REPLY></REQUEST> */
data class RequestReply<REQUEST, REPLY>(var request: REQUEST?, var reply: REPLY?) : Serializable