/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform?ment aux dispositions de la Licence Publique G?n?rale GNU, telle que publi?e par la Free Software Foundation ; version 2 de la licence, ou encore (? votre choix) toute version ult?rieure.

Ce programme est distribu? dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m?me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d?tail, voir la Licence Publique G?n?rale GNU .

Vous devez avoir re?u un exemplaire de la Licence Publique G?n?rale GNU en m?me temps que ce programme ; si ce n'est pas le cas, ?crivez ? la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class WXSAttributeGroup extends WXSAnnotated implements Parent {
    
    protected List<WXSThing> attrDecls; // attrDecls: (attribute|attributeGroup)*
    protected String name = null;
    protected String ref = null;
    
    protected WXSAttributeGroup wxsRef = null;
    protected Element domElement;
    protected Parent parent; // WXSComplexType | WXSRestriction | WXSExtension | WXSAttributeGroup | WXSRedefine
    protected WXSSchema schema;
    
    
    public WXSAttributeGroup(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        attrDecls = new ArrayList<WXSThing>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("attribute".equals(n.getLocalName()))
                    attrDecls.add(new WXSAttribute((Element)n, this, schema));
                else if ("attributeGroup".equals(n.getLocalName()))
                    attrDecls.add(new WXSAttributeGroup((Element)n, this, schema));
            }
        }
        if (el.getAttributeNode("name") != null)
            name = el.getAttribute("name");
        if (el.getAttributeNode("ref") != null)
            ref = el.getAttribute("ref");
        
        domElement = el;
        this.parent = parent;
        this.schema = schema;
    }
    
    public String getNamespace() {
        return(schema.getTargetNamespace());
    }
    
    public Parent getParent() {
        return(parent);
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        for (WXSThing attrDecl: attrDecls) {
            if (attrDecl instanceof WXSAttribute)
                ((WXSAttribute)attrDecl).resoudreReferences(schema);
            else if (attrDecl instanceof WXSAttributeGroup)
                ((WXSAttributeGroup)attrDecl).resoudreReferences(schema, redefine);
        }
        if (ref != null) {
            final String prefixe = JaxeWXS.prefixeNom(ref);
            final String tns;
            if ("xml".equals(prefixe))
                tns = "http://www.w3.org/XML/1998/namespace";
            else
                tns = domElement.lookupNamespaceURI(prefixe);
            wxsRef = schema.resoudreReferenceGroupeAttributs(JaxeWXS.valeurLocale(ref), tns, redefine);
        }
    }
    
    public String getName() {
        if (name == null && wxsRef != null)
            return(wxsRef.getName());
        return(name);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        if (parent != null)
            return(parent.listeElementsParents());
        return(new ArrayList<WXSElement>());
    }
    
    public ArrayList<WXSAttribute> listeAttributs() {
        if (wxsRef != null)
            return(wxsRef.listeAttributs());
        final ArrayList<WXSAttribute> liste = new ArrayList<WXSAttribute>();
        for (WXSThing attrDecl: attrDecls) {
            if (attrDecl instanceof WXSAttribute)
                liste.add((WXSAttribute)attrDecl);
            else if (attrDecl instanceof WXSAttributeGroup)
                liste.addAll(((WXSAttributeGroup)attrDecl).listeAttributs());
        }
        return(liste);
    }
}
