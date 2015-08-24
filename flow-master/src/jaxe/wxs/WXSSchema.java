/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;


import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import jaxe.JaxeException;


public class WXSSchema implements WXSThing {
    
    protected ArrayList<WXSInclude> includes;
    protected ArrayList<WXSImport> imports;
    protected ArrayList<WXSRedefine> redefines;
    protected LinkedHashMap<String, WXSSimpleType> simpleTypes;
    protected LinkedHashMap<String, WXSComplexType> complexTypes;
    protected LinkedHashMap<String, WXSGroup> groups;
    protected LinkedHashMap<String, WXSAttributeGroup> attributeGroups;
    protected LinkedHashMap<String, WXSElement> elements;
    protected LinkedHashMap<String, WXSAttribute> attributes;
    protected String targetNamespace = null;
    protected String attributeFormDefault = null;
    protected String elementFormDefault = null;
    
    protected URL url;
    protected JaxeWXS jwxs;
    protected ArrayList<WXSSchema> schemasInclu;
    protected WXSSchema schemaParent;
    protected HashMap<String, String> espaceVersPrefixe; // associations espace de noms -> préfixe
    
    
    public WXSSchema(final Element el, final URL url, final JaxeWXS jwxs, final WXSSchema schemaParent) {
        this.url = url;
        this.jwxs = jwxs;
        this.schemaParent = schemaParent;
        includes = new ArrayList<WXSInclude>();
        imports = new ArrayList<WXSImport>();
        redefines = new ArrayList<WXSRedefine>();
        simpleTypes = new LinkedHashMap<String, WXSSimpleType>();
        complexTypes = new LinkedHashMap<String, WXSComplexType>();
        groups = new LinkedHashMap<String, WXSGroup>();
        attributeGroups = new LinkedHashMap<String, WXSAttributeGroup>();
        elements = new LinkedHashMap<String, WXSElement>();
        attributes = new LinkedHashMap<String, WXSAttribute>();
        schemasInclu = new ArrayList<WXSSchema>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("include".equals(n.getLocalName()))
                    includes.add(new WXSInclude((Element)n, this));
                else if ("import".equals(n.getLocalName()))
                    imports.add(new WXSImport((Element)n, this));
                else if ("redefine".equals(n.getLocalName())) {
                    final WXSRedefine redefine = new WXSRedefine((Element)n, this);
                    redefines.add(redefine);
                    for (WXSThing redefinable : redefine.getRedefinables()) {
                        if (redefinable instanceof WXSSimpleType) {
                            final WXSSimpleType simpleType = (WXSSimpleType)redefinable;
                            simpleTypes.put(simpleType.getName(), simpleType);
                        } else if (redefinable instanceof WXSComplexType) {
                            final WXSComplexType complexType = (WXSComplexType)redefinable;
                            complexTypes.put(complexType.getName(), complexType);
                        } else if (redefinable instanceof WXSGroup) {
                            final WXSGroup group = (WXSGroup)redefinable;
                            groups.put(group.getName(), group);
                        } else if (redefinable instanceof WXSAttributeGroup) {
                            final WXSAttributeGroup attributeGroup = (WXSAttributeGroup)redefinable;
                            attributeGroups.put(attributeGroup.getName(), attributeGroup);
                        }
                    }
                } else if ("simpleType".equals(n.getLocalName())) {
                    final WXSSimpleType simpleType = new WXSSimpleType((Element)n, null, this);
                    simpleTypes.put(simpleType.getName(), simpleType);
                } else if ("complexType".equals(n.getLocalName())) {
                    final WXSComplexType complexType = new WXSComplexType((Element)n, null, this);
                    complexTypes.put(complexType.getName(), complexType);
                } else if ("group".equals(n.getLocalName())) {
                    final WXSGroup group = new WXSGroup((Element)n, null, this);
                    groups.put(group.getName(), group);
                } else if ("attributeGroup".equals(n.getLocalName())) {
                    final WXSAttributeGroup attributeGroup = new WXSAttributeGroup((Element)n, null, this);
                    attributeGroups.put(attributeGroup.getName(), attributeGroup);
                } else if ("element".equals(n.getLocalName())) {
                    final WXSElement element = new WXSElement((Element)n, null, this);
                    elements.put(element.getName(), element);
                } else if ("attribute".equals(n.getLocalName())) {
                    final WXSAttribute attribute = new WXSAttribute((Element)n, null, this);
                    attributes.put(attribute.getName(), attribute);
                }
            }
        }
        if (el.getAttributeNode("targetNamespace") != null) {
            targetNamespace = el.getAttribute("targetNamespace");
            if ("".equals(targetNamespace))
                targetNamespace = null;
        }
        if (el.getAttributeNode("attributeFormDefault") != null)
            attributeFormDefault = el.getAttribute("attributeFormDefault");
        if (el.getAttributeNode("elementFormDefault") != null)
            elementFormDefault = el.getAttribute("elementFormDefault");
        
        espaceVersPrefixe = new HashMap<String, String>();
        final NamedNodeMap latt = el.getAttributes();
        for (int i=0; i<latt.getLength(); i++) {
            final Node n = latt.item(i);
            final String nomatt = n.getNodeName();
            final String valatt = n.getNodeValue();
            if (nomatt.startsWith("xmlns:")) {
                final String prefixe = nomatt.substring(6);
                espaceVersPrefixe.put(valatt, prefixe);
            }
        }
    }
    
    protected void inclusions() throws JaxeException {
        for (WXSInclude include: includes)
            include.inclusions(this);
        for (WXSImport imp: imports)
            imp.inclusions(this);
        for (WXSRedefine redefine: redefines)
            redefine.inclusions(this);
    }
    
    /*
    public Set<WXSSimpleType> getTopSimpleTypes() {
        return(simpleTypes);
    }
    
    public Set<WXSComplexType> getTopComplexTypes() {
        return(complexTypes);
    }
    
    public Collection<WXSGroup> getTopGroups() {
        return(groups.values());
    }
    
    public Set<WXSAttributeGroup> getTopAttributeGroups() {
        return(attributeGroups);
    }
    */
    public Collection<WXSElement> getTopElements() {
        return(elements.values());
    }
    
    public Collection<WXSAttribute> getTopAttributes() {
        return(attributes.values());
    }
    
    public String getTargetNamespace() {
        return(targetNamespace);
    }
    
    public String getAttributeFormDefault() {
        return(attributeFormDefault);
    }
    
    public String getElementFormDefault() {
        return(elementFormDefault);
    }
    
    public URL getURL() {
        return(url);
    }
    
    protected WXSSchema nouveauSchemaInclu(final String schemaLocation, final String espaceImport, final WXSSchema schemaParent) throws JaxeException {
        final WXSSchema schemaInclu = jwxs.nouveauSchemaInclu(url, schemaLocation, espaceImport, schemaParent);
        if (schemaInclu != null && !schemasInclu.contains(schemaInclu))
            schemasInclu.add(schemaInclu);
        return(schemaInclu);
    }
    
    public void resoudreReferences() {
        for (WXSSimpleType simpleType: simpleTypes.values())
            simpleType.resoudreReferences(this, simpleType.getParent() instanceof WXSRedefine ? simpleType : null);
        for (WXSComplexType complexType: complexTypes.values())
            complexType.resoudreReferences(this, complexType.getParent() instanceof WXSRedefine ? complexType : null);
        for (WXSGroup group: groups.values())
            group.resoudreReferences(this, group.getParent() instanceof WXSRedefine ? group : null);
        for (WXSAttributeGroup attributeGroup: attributeGroups.values())
            attributeGroup.resoudreReferences(this, attributeGroup.getParent() instanceof WXSRedefine ? attributeGroup : null);
        for (WXSElement element: elements.values())
            element.resoudreReferences(this, null);
        for (WXSAttribute attribute: attributes.values())
            attribute.resoudreReferences(this);
    }
    
    public String prefixeEspace(final String ns) {
        return(espaceVersPrefixe.get(ns));
    }
    
    public String espacePrefixe(final String prefixe) {
        if (prefixe == null)
            return(null);
        final Set<Map.Entry<String,String>> associations = espaceVersPrefixe.entrySet();
        for (Map.Entry<String,String> me : associations)
            if (prefixe.equals(me.getValue()))
                return(me.getKey());
        return(null);
    }
    
    public Set<WXSElement> listeTousElements() {
        final LinkedHashSet<WXSElement> liste = new LinkedHashSet<WXSElement>();
        for (WXSComplexType complexType: complexTypes.values())
            liste.addAll(complexType.listeTousElements());
        for (WXSGroup group: groups.values())
            liste.addAll(group.listeTousElements());
        for (WXSElement element: elements.values())
            liste.addAll(element.listeTousElements());
        return(liste);
    }
    
    public Set<ToutElement> listeAny(final String namespace) {
        return(jwxs.listeAny(namespace, targetNamespace));
    }
    
    public WXSElement resoudreReferenceElement(final String nomLocal, final String espace) {
        return((WXSElement)resoudreReference(nomLocal, espace, null, null, WXSElement.class));
    }
    
    public WXSType resoudreReferenceType(final String nomLocal, final String espace, final WXSThing redefine) {
        return((WXSType)resoudreReference(nomLocal, espace, null, redefine, WXSType.class));
    }
    
    public WXSGroup resoudreReferenceGroupe(final String nomLocal, final String espace, final WXSThing redefine) {
        return((WXSGroup)resoudreReference(nomLocal, espace, null, redefine, WXSGroup.class));
    }
    
    public WXSAttributeGroup resoudreReferenceGroupeAttributs(final String nomLocal, final String espace, final WXSThing redefine) {
        return((WXSAttributeGroup)resoudreReference(nomLocal, espace, null, redefine, WXSAttributeGroup.class));
    }
    
    public WXSAttribute resoudreReferenceAttribut(final String nomLocal, final String espace) {
        return((WXSAttribute)resoudreReference(nomLocal, espace, null, null, WXSAttribute.class));
    }
    
    protected WXSThing resoudreReference(final String nomLocal, final String espace, final HashSet<WXSSchema> exclure, final WXSThing redefine, final Class classe) {
        if (nomLocal == null)
            return(null);
        HashSet<WXSSchema> exclure2 = null;
        if (schemaParent != null && (exclure == null || !exclure.contains(schemaParent))) {
            if (exclure2 == null) {
                if (exclure == null)
                    exclure2 = new HashSet<WXSSchema>();
                else
                    exclure2 = exclure;
                exclure2.add(this);
            }
            final WXSThing thing = schemaParent.resoudreReference(nomLocal, espace, exclure2, redefine, classe);
            if (thing != null)
                return(thing);
        }
        if ((espace == null && targetNamespace == null) || (espace != null && espace.equals(targetNamespace))) {
            WXSThing thing;
            if (classe == WXSElement.class)
                thing = elements.get(nomLocal);
            else if (classe == WXSType.class) {
                thing = complexTypes.get(nomLocal);
                if (thing != null && thing != redefine)
                    return(thing);
                thing = simpleTypes.get(nomLocal);
            } else if (classe == WXSGroup.class)
                thing = groups.get(nomLocal);
            else if (classe == WXSAttributeGroup.class)
                thing = attributeGroups.get(nomLocal);
            else if (classe == WXSAttribute.class)
                thing = attributes.get(nomLocal);
            else
                thing = null;
            if (thing != null && thing != redefine)
                return(thing);
        }
        for (WXSSchema schemaInclu: schemasInclu) {
            if (exclure == null || !exclure.contains(schemaInclu)) {
                if (exclure2 == null) {
                    if (exclure == null)
                        exclure2 = new HashSet<WXSSchema>();
                    else
                        exclure2 = exclure;
                    exclure2.add(this);
                }
                final WXSThing thing = schemaInclu.resoudreReference(nomLocal, espace, exclure2, redefine, classe);
                if (thing  != null)
                    return(thing);
            }
        }
        return(null);
    }
    
    public String titreElement(final Element refElement) {
        return(jwxs.titreElement(refElement));
    }
    
}
