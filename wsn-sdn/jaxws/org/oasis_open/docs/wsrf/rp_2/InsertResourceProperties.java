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
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Insert"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"insert"
})
@XmlRootElement(name = "InsertResourceProperties")
public class InsertResourceProperties {

	@XmlElement(name = "Insert", required = true)
	protected InsertType insert;

	/**
	 * Gets the value of the insert property.
	 *
	 * @return possible object is
	 * {@link InsertType }
	 */
	public InsertType getInsert() {
		return insert;
	}

	/**
	 * Sets the value of the insert property.
	 *
	 * @param value allowed object is
	 *              {@link InsertType }
	 */
	public void setInsert(InsertType value) {
		this.insert = value;
	}

}
