package org.jsmpp.session;

import java.io.IOException;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.QuerySmResp;
import org.jsmpp.bean.SubmitSmResp;
import org.jsmpp.extra.ProcessRequestException;

/**
 * <tt>ResponseHandler</tt> provide interface to handle response of the
 * session routines.
 * 
 * @author uudashr
 * @version 1.0
 * @since 2.0
 * 
 */
public interface ClientResponseHandler extends ResponseHandler {

    public void processDeliverSm(DeliverSm deliverSm) throws ProcessRequestException;

    public void processSubmitSmResp(SubmitSmResp resp) throws IOException;
    
    public void processQuerySmResp(QuerySmResp resp) throws IOException;

}