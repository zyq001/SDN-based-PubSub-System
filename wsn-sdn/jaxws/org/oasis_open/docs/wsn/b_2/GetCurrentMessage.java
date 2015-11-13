package org.oasis_open.docs.wsn.b_2;

import org.w3c.dom.Element;

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
 *         &lt;element name="Topic" type="{http://docs.oasis-open.org/wsn/b-2}TopicExpressionType"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"topic",
		"any"
})
@XmlRootElement(name = "GetCurrentMessage")
public class GetCurrentMessage {

	@XmlElement(name = "Topic", required = true)
	protected TopicExpressionType topic;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	/**
	 * Gets the value of the topic property.
	 *
	 * @return possible object is
	 * {@link TopicExpressionType }
	 */
	public TopicExpressionType getTopic() {
		return topic;
	}

	/**
	 * Sets the value of the topic property.
	 *
	 * @param value allowed object is
	 *              {@link TopicExpressionType }
	 */
	public void setTopic(TopicExpressionType value) {
		this.topic = value;
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
