//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.28 at 10:27:21 AM CEST 
//


package fabermaster.utils.templater.model.restful;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for api complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="api">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="absolutePath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="object" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="component" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="entity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="exposures" type="{}exposureType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "api", propOrder = {
    "absolutePath",
    "object",
    "component",
    "entity",
    "exposures"
})
public class Api
    implements Serializable
{

    @XmlElement(required = true)
    protected String absolutePath;
    @XmlElement(required = true)
    protected String object;
    @XmlElement(required = true)
    protected String component;
    @XmlElement(required = true)
    protected String entity;
    @XmlElement(required = true)
    protected List<ExposureType> exposures;

    /**
     * Gets the value of the absolutePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Sets the value of the absolutePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbsolutePath(String value) {
        this.absolutePath = value;
    }

    /**
     * Gets the value of the object property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObject() {
        return object;
    }

    /**
     * Sets the value of the object property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObject(String value) {
        this.object = value;
    }

    /**
     * Gets the value of the component property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComponent() {
        return component;
    }

    /**
     * Sets the value of the component property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComponent(String value) {
        this.component = value;
    }

    /**
     * Gets the value of the entity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Sets the value of the entity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntity(String value) {
        this.entity = value;
    }

    /**
     * Gets the value of the exposures property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exposures property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExposures().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExposureType }
     * 
     * 
     */
    public List<ExposureType> getExposures() {
        if (exposures == null) {
            exposures = new ArrayList<ExposureType>();
        }
        return this.exposures;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("Api [absolutePath=");
      builder.append(absolutePath);
      builder.append(", object=");
      builder.append(object);
      builder.append(", component=");
      builder.append(component);
      builder.append(", entity=");
      builder.append(entity);
      builder.append(", exposures=");
      builder.append(exposures);
      builder.append("]");
      return builder.toString();
    }

    
}
