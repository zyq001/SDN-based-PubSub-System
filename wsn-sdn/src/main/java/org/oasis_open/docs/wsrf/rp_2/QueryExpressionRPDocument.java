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
 *       &lt;sequence>
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}QueryExpressionDialect" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"queryExpressionDialect"
})
@XmlRootElement(name = "QueryExpressionRPDocument")
public class QueryExpressionRPDocument {

	@XmlElement(name = "QueryExpressionDialect")
	@XmlSchemaType(name = "anyURI")
	protected List<String> queryExpressionDialect;

	/**
	 * Gets the value of the queryExpressionDialect property.
	 * <p/>
	 * <p/>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the queryExpressionDialect property.
	 * <p/>
	 * <p/>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getQueryExpressionDialect().add(newItem);
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Objects of the following type(s) are allowed in the list
	 * {@link String }
	 */
	public List<String> getQueryExpressionDialect() {
		if (queryExpressionDialect == null) {
			queryExpressionDialect = new ArrayList<String>();
		}
		return this.queryExpressionDialect;
	}

}
