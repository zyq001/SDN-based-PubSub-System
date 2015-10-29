package org.oasis_open.docs.wsn.b_2;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="Local" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsn/b-2}NotificationMessage" maxOccurs="unbounded"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"local",
		"notificationMessage",
		"any"
})
@XmlRootElement(name = "Notify")
public class Notify {

	@XmlElement(name = "Local")
	protected Boolean local;
	@XmlElement(name = "NotificationMessage", required = true)
	protected List<NotificationMessageHolderType> notificationMessage;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the local property.
	 *
	 * @return possible object is
	 * {@link Boolean }
	 */
	public Boolean isLocal() {
		return local;
	}

	/**
	 * Sets the value of the local property.
	 *
	 * @param value allowed object is
	 *              {@link Boolean }
	 */
	public void setLocal(Boolean value) {
		this.local = value;
	}

	/**
	 * Gets the value of the notificationMessage property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the notificationMessage property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getNotificationMessage().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link NotificationMessageHolderType }
	 */
	public List<NotificationMessageHolderType> getNotificationMessage() {
		if (notificationMessage == null) {
			notificationMessage = new ArrayList<NotificationMessageHolderType>();
		}
		return this.notificationMessage;
	}

	/**
	 * Gets the value of the any property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the any property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getAny().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Object }
	 * {@link Element }
	 */
	public List<Object> getAny() {
		if (any == null) {
			any = new ArrayList<Object>();
		}
		return this.any;
	}

}
