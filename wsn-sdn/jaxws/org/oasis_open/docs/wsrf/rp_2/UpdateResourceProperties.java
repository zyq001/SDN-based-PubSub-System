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
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Update"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"update"
})
@XmlRootElement(name = "UpdateResourceProperties")
public class UpdateResourceProperties {

	@XmlElement(name = "Update", required = true)
	protected UpdateType update;

	/**
	 * Gets the value of the update property.
	 *
	 * @return possible object is
	 * {@link UpdateType }
	 */
	public UpdateType getUpdate() {
		return update;
	}

	/**
	 * Sets the value of the update property.
	 *
	 * @param value allowed object is
	 *              {@link UpdateType }
	 */
	public void setUpdate(UpdateType value) {
		this.update = value;
	}

}
