package org.oasis_open.docs.wsrf.rp_2;

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
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Insert"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Update"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}Delete"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"insertOrUpdateOrDelete"
})
@XmlRootElement(name = "SetResourceProperties")
public class SetResourceProperties {

	@XmlElements({
			@XmlElement(name = "Delete", type = DeleteType.class),
			@XmlElement(name = "Update", type = UpdateType.class),
			@XmlElement(name = "Insert", type = InsertType.class)
	})
	protected List<Object> insertOrUpdateOrDelete;

	/**
	 * Gets the value of the insertOrUpdateOrDelete property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the insertOrUpdateOrDelete property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getInsertOrUpdateOrDelete().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link DeleteType }
	 * {@link UpdateType }
	 * {@link InsertType }
	 */
	public List<Object> getInsertOrUpdateOrDelete() {
		if (insertOrUpdateOrDelete == null) {
			insertOrUpdateOrDelete = new ArrayList<Object>();
		}
		return this.insertOrUpdateOrDelete;
	}

}
