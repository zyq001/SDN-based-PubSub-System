
package jaxe.wxs;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import jaxe.JaxeException;


public class WXSRedefine implements WXSThing, Parent {
    
    // annotations : inutile ici
    protected List<WXSThing> redefinables;
     // (simpleType|complexType|group|attributeGroup)
    protected String schemaLocation = null; // URI
    
    protected WXSSchema schemaInclu = null;
    protected WXSSchema schema;
    
    
    public WXSRedefine(final Element el, final WXSSchema schema) {
        redefinables = new ArrayList<WXSThing>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("simpleType".equals(n.getLocalName()))
                    redefinables.add(new WXSSimpleType((Element)n, this, schema));
                else if ("complexType".equals(n.getLocalName()))
                    redefinables.add(new WXSComplexType((Element)n, this, schema));
                else if ("group".equals(n.getLocalName()))
                    redefinables.add(new WXSGroup((Element)n, this, schema));
                else if ("attributeGroup".equals(n.getLocalName()))
                    redefinables.add(new WXSAttributeGroup((Element)n, this, schema));
            }
        }
        if (el.getAttributeNode("schemaLocation") != null)
            schemaLocation = el.getAttribute("schemaLocation");
        
        this.schema = schema;
    }
    
    protected void inclusions(final WXSSchema schema) throws JaxeException {
        schemaInclu = schema.nouveauSchemaInclu(schemaLocation, null, schema);
    }
    
    public List<WXSThing> getRedefinables() {
        return(redefinables);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        return(new ArrayList<WXSElement>());
    }
    
    public String getNamespace() {
        return(schema.getTargetNamespace());
    }
    
}
