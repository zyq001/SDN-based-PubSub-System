package org.oasis_open.docs.wsrf.rp_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ResourcePropertyValueChangeNotificationType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ResourcePropertyValueChangeNotificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OldValues" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="NewValues">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourcePropertyValueChangeNotificationType", propOrder = {
		"oldValues",
		"newValues"
})
public class ResourcePropertyValueChangeNotificationType {

	@XmlElementRef(name = "OldValues", namespace = "http://docs.oasis-open.org/wsrf/rp-2", type = JAXBElement.class)
	protected JAXBElement<OldValues> oldValues;
	@XmlElement(name = "NewValues", required = true, nillable = true)
	protected NewValues newValues;

	/**
	 * Gets the value of the oldValues property.
	 *
	 * @return possible object is
	 * {@link JAXBElement }{@code <}{@link OldValues }{@code >}
	 */
	public JAXBElement<OldValues> getOldValues() {
		return oldValues;
	}

	/**
	 * Sets the value of the oldValues property.
	 *
	 * @param value allowed object is
	 *              {@link JAXBElement }{@code <}{@link OldValues }{@code >}
	 */
	public void setOldValues(JAXBElement<OldValues> value) {
		this.oldValues = ((JAXBElement<OldValues>) value);
	}

	/**
	 * Gets the value of the newValues property.
	 *
	 * @return possible object is
	 * {@link NewValues }
	 */
	public NewValues getNewValues() {
		return newValues;
	}

	/**
	 * Sets the value of the newValues property.
	 *
	 * @param value allowed object is
	 *              {@link NewValues }
	 */
	public void setNewValues(NewValues value) {
		this.newValues = value;
	}


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
	 *         &lt;any maxOccurs="unbounded"/>
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
	public static class NewValues {

		@XmlAnyElement(lax = true)
		protected List<Object> any;

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
		 */
		public List<Object> getAny() {
			if (any == null) {
				any = new ArrayList<Object>();
			}
			return this.any;
		}

	}


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
	 *         &lt;any maxOccurs="unbounded"/>
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
	public static class OldValues {

		@XmlAnyElement(lax = true)
		protected List<Object> any;

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
		 */
		public List<Object> getAny() {
			if (any == null) {
				any = new ArrayList<Object>();
			}
			return this.any;
		}

	}

}
