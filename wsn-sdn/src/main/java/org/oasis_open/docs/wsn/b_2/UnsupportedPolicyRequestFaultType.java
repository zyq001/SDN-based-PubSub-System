package org.oasis_open.docs.wsn.b_2;

import org.oasis_open.docs.wsrf.bf_2.BaseFaultType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for UnsupportedPolicyRequestFaultType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="UnsupportedPolicyRequestFaultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsrf/bf-2}BaseFaultType">
 *       &lt;sequence>
 *         &lt;element name="UnsupportedPolicy" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsupportedPolicyRequestFaultType", propOrder = {
		"unsupportedPolicy"
})
public class UnsupportedPolicyRequestFaultType
		extends BaseFaultType {

	@XmlElement(name = "UnsupportedPolicy")
	protected List<QName> unsupportedPolicy;

	/**
	 * Gets the value of the unsupportedPolicy property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the unsupportedPolicy property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getUnsupportedPolicy().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link QName }
	 */
	public List<QName> getUnsupportedPolicy() {
		if (unsupportedPolicy == null) {
			unsupportedPolicy = new ArrayList<QName>();
		}
		return this.unsupportedPolicy;
	}

}
