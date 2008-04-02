package org.jsmpp.session.state.server;

import java.io.IOException;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.Command;
import org.jsmpp.bean.EnquireLinkResp;
import org.jsmpp.bean.PDU;
import org.jsmpp.bean.UnbindResp;
import org.jsmpp.extra.PendingResponse;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.ServerResponseHandler;
import org.jsmpp.session.state.SMPPSessionState;
import org.jsmpp.util.PDUDecomposer;
import org.jsmpp.util.PDUDecomposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMPPServerSessionClosed implements SMPPSessionState {
    final protected ServerResponseHandler responseHandler;
    private static final Logger logger = LoggerFactory.getLogger(SMPPServerSessionClosed.class);
    protected PDUDecomposer pduDecomposer = PDUDecomposer.getInstance();

    public SMPPServerSessionClosed(ServerResponseHandler responseHandler) {        
        this.responseHandler = responseHandler;
    }

    public SessionState getSessionState() {
        return SessionState.CLOSED;
    }

    public void process(PDU pdu) throws IOException {
        switch (pdu.getCommandId()) {
        case SMPPConstant.CID_BIND_RECEIVER:
        case SMPPConstant.CID_BIND_TRANSMITTER:
        case SMPPConstant.CID_BIND_TRANSCEIVER:
            processBind(pdu);
            break;
        case SMPPConstant.CID_GENERIC_NACK:
            processGenericNack(pdu);
            break;
        case SMPPConstant.CID_ENQUIRE_LINK:
            processEnquireLink(pdu);
            break;
        case SMPPConstant.CID_ENQUIRE_LINK_RESP:
            processEnquireLinkResp(pdu);
            break;
        case SMPPConstant.CID_SUBMIT_SM:
            processSubmitSm(pdu);
            break;
        case SMPPConstant.CID_QUERY_SM:
            processQuerySm(pdu);
            break;
        case SMPPConstant.CID_DELIVER_SM_RESP:
            processDeliverSmResp(pdu);
            break;
        case SMPPConstant.CID_UNBIND:
            processUnbind(pdu);
            break;
        case SMPPConstant.CID_UNBIND_RESP:
            processUnbindResp(pdu);
            break;
        default:
            processUnknownCid(pdu);
        }
    }

    public void processEnquireLink(PDU pdu) throws IOException {
        responseHandler.sendEnquireLinkResp(pdu.getCommand().getSequenceNumber());
    }

    public void processEnquireLinkResp(PDU pdu) {
        PendingResponse<Command> pendingResp = responseHandler.removeSentItem(pdu.getCommand().getSequenceNumber());
        if (pendingResp != null) {
            EnquireLinkResp resp = pduDecomposer.enquireLinkResp(pdu);
            pendingResp.done(resp);
        } else {
            logger.error("No request found for " + pdu.getCommand());
        }
    }

    public void processUnbind(PDU pdu) throws IOException {
        Command pduHeader = pdu.getCommand();
        logger.info("Receving unbind request");
        try {
            responseHandler.sendUnbindResp(pduHeader.getSequenceNumber());
        } finally {
            responseHandler.notifyUnbonded();
        }
    }

    public void processUnbindResp(PDU pdu) {
        PendingResponse<Command> pendingResp = responseHandler.removeSentItem(pdu.getCommand().getSequenceNumber());
        if (pendingResp != null) {
            UnbindResp resp = pduDecomposer.unbindResp(pdu);
            pendingResp.done(resp);
        } else {
            logger.error("No request found for " + pdu);
        }
    }

    public void processUnknownCid(PDU pdu) throws IOException {
        logger.warn("Received unknown CommandId " + pdu.getCommand().getCommandIdAsHex());
        responseHandler.sendGenerickNack(SMPPConstant.STAT_ESME_RINVCMDID, pdu.getCommand().getSequenceNumber());
    }

    public void processGenericNack(PDU pdu) {
        Command pduHeader = pdu.getCommand();
        PendingResponse<Command> pendingResp = responseHandler.removeSentItem(pduHeader.getSequenceNumber());
        if (pendingResp != null) {
            pendingResp.doneWithInvalidResponse(new InvalidResponseException("Receive generic_nack with command_status " + pduHeader.getCommandStatusAsHex()));
        }
    }

    public void processBind(PDU pdu) throws IOException {
        throw new IOException("Invalid operation for " + getSessionState() + " session state");
    }

    public void processDeliverSmResp(PDU pdu) throws IOException {
        throw new IOException("Invalid operation for " + getSessionState() + " session state");
    }

    public void processQuerySm(PDU pdu) throws IOException {
        throw new IOException("Invalid operation for " + getSessionState() + " session state");
    }

    public void processSubmitSm(PDU pdu) throws IOException {
        throw new IOException("Invalid operation for " + getSessionState() + " session state");
    }

}