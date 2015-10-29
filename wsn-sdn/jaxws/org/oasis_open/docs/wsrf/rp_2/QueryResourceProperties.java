package org.oasis_open.docs.wsrf.rp_2;

import javax.xml.bind.annotation.*;


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
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}QueryExpression"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"queryExpression"
})
@XmlRootElement(name = "QueryResourceProperties")
public class QueryResourceProperties {

	@XmlElement(name = "QueryExpression", required = true)
	protected QueryExpressionType queryExpression;

	/**
	 * Gets the value of the queryExpression property.
	 *
	 * @return possible object is
	 * {@link QueryExpressionType }
	 */
	public QueryExpressionType getQueryExpression() {
		return queryExpression;
	}

	/**
	 * Sets the value of the queryExpression property.
	 *
	 * @param value allowed object is
	 *              {@link QueryExpressionType }
	 */
	public void setQueryExpression(QueryExpressionType value) {
		this.queryExpression = value;
	}

}
