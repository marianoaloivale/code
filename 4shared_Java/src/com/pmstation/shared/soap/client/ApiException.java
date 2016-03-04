
package com.pmstation.shared.soap.client;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.1-02/02/2007 03:56 AM(vivekp)-FCS
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "ApiException", targetNamespace = "http://api.soap.shared.pmstation.com/")
public class ApiException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private FaultBean faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public ApiException(String message, FaultBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ApiException(String message, FaultBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: com.pmstation.shared.soap.client.FaultBean
     */
    public FaultBean getFaultInfo() {
        return faultInfo;
    }

}
