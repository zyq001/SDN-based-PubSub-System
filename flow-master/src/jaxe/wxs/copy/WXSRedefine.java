/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

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
    protected List<WXSThing> redefinables; // (simpleType|complexType|group|attributeGroup)
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
