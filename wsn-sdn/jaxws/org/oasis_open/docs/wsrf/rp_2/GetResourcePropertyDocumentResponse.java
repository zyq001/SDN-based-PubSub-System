package org.oasis_open.docs.wsrf.rp_2;

/**
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"any"
})
@XmlRootElement(name = "GetResourcePropertyDocumentResponse")
public class GetResourcePropertyDocumentResponse {

	@XmlAnyElement(lax = true)
	protected Object any;

	/**
	 * Gets the value of the any property.
	 *
	 * @return possible object is
	 * {@link Object }
	 */
	public Object getAny() {
		return any;
	}

	/**
	 * Sets the value of the any property.
	 *
	 * @param value allowed object is
	 *              {@link Object }
	 */
	public void setAny(Object value) {
		this.any = value;
	}

}
