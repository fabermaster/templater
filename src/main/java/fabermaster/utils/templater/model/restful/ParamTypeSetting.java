//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.28 at 04:05:02 PM CEST 
//


package fabermaster.utils.templater.model.restful;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paramTypeSetting complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paramTypeSetting">
 *   &lt;complexContent>
 *     &lt;extension base="{}paramType">
 *       &lt;sequence>
 *         &lt;element name="toComplete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paramTypeSetting", propOrder = {
    "toComplete"
})
public class ParamTypeSetting
    extends ParamType
    implements Serializable
{

    protected Boolean toComplete;

    /**
     * Gets the value of the toComplete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isToComplete() {
        return toComplete;
    }

    /**
     * Sets the value of the toComplete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setToComplete(Boolean value) {
        this.toComplete = value;
    }

}
