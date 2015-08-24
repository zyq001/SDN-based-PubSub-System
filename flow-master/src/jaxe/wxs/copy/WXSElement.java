/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.log4j.Logger;


public class WXSElement extends WXSAnnotated implements ToutElement, AvecSousElements, Parent {
    
    private static final Logger LOG = Logger.getLogger(WXSElement.class);
    
    // (simpleType|complexType)?, (unique|key|keyref)*
    protected WXSSimpleType simpleType = null;
    protected WXSComplexType complexType = null;
    protected List<WXSThing> identityConstraints; // (unique|key|keyref)*
    protected String name = null;
    protected String ref = null;
    protected String type = null;
    protected String substitutionGroup = null;
    protected int minOccurs = 1;
    protected int maxOccurs = 1;
    protected String defaultAtt = null;
    protected String fixed = null;
    protected boolean abstractAtt = false;
    protected String form = null; // (qualified|unqualified)
    
    protected WXSElement wxsRef = null;
    protected WXSElement wxsSubstitutionGroup = null;
    protected Element domElement;
    protected Parent parent; // WXSAll | WXSChoice | WXSSequence
    protected WXSSchema schema;
    protected List<WXSThing> references; // WXSElement | WXSAny
    protected List<WXSElement> substitutions;
    protected List<WXSElement> correspondant; // cache des �l�ments correspondant
    protected ArrayList<ToutElement> sousElements; // cache des sous-�l�ments
    
    
    public WXSElement(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        identityConstraints = new ArrayList<WXSThing>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("simpleType".equals(n.getLocalName()))
                    simpleType = new WXSSimpleType((Element)n, this, schema);
                else if ("complexType".equals(n.getLocalName()))
                    complexType = new WXSComplexType((Element)n, this, schema);
                else if ("unique".equals(n.getLocalName()))
                    identityConstraints.add(new WXSUnique((Element)n));
                else if ("key".equals(n.getLocalName()))
                    identityConstraints.add(new WXSKey((Element)n));
                else if ("keyref".equals(n.getLocalName()))
                    identityConstraints.add(new WXSKeyref((Element)n));
            }
        }
        if (el.getAttributeNode("name") != null)
            name = el.getAttribute("name");
        if (el.getAttributeNode("ref") != null)
            ref = el.getAttribute("ref");
        if (el.getAttributeNode("type") != null)
            type = el.getAttribute("type");
        if (el.getAttributeNode("substitutionGroup") != null)
            substitutionGroup = el.getAttribute("substitutionGroup");
        try {
            if (el.getAttributeNode("minOccurs") != null)
                minOccurs = Integer.parseInt(el.getAttribute("minOccurs"));
            if (el.getAttributeNode("maxOccurs") != null) {
                if ("unbounded".equals(el.getAttribute("maxOccurs")))
                    maxOccurs = Integer.MAX_VALUE;
                else
                    maxOccurs = Integer.parseInt(el.getAttribute("maxOccurs"));
            }
        } catch (NumberFormatException ex) {
        }
        if (el.getAttributeNode("default") != null)
            defaultAtt = el.getAttribute("default");
        if (el.getAttributeNode("fixed") != null)
            fixed = el.getAttribute("fixed");
        if (el.getAttributeNode("abstract") != null)
            abstractAtt = "true".equals(el.getAttribute("abstract")) || "1".equals(el.getAttribute("abstract"));
        if (el.getAttributeNode("form") != null)
            form = el.getAttribute("form");
        
        domElement = el;
        this.parent = parent;
        this.schema = schema;
        references = null;
        substitutions = null;
        sousElements = null;
        correspondant = null;
    }
    
    public String getName() {
        if (name == null && wxsRef != null)
            return(wxsRef.getName());
        return(name);
    }
    
    public String getRef() {
        return(ref);
    }
    
    public int getMinOccurs() {
        return(minOccurs);
    }
    
    public int getMaxOccurs() {
        return(maxOccurs);
    }
    
    public boolean getAbstract() {
        return(abstractAtt);
    }
    
    
    public Element getDOMElement() {
        return(domElement);
    }
    
    public String getNamespace() {
        boolean qualified;
        if (schema.getTopElements().contains(this))
            qualified = true;
        else if (form != null)
            qualified = "qualified".equals(form);
        else
            qualified = "qualified".equals(schema.getElementFormDefault());
        if (qualified)
            return(schema.getTargetNamespace());
        else
            return(null);
    }
    
    public Parent getParent() {
        return(parent);
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (simpleType != null)
            simpleType.resoudreReferences(schema, null);
        if (complexType != null)
            complexType.resoudreReferences(schema, redefine);
        if (ref != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(ref));
            wxsRef = schema.resoudreReferenceElement(JaxeWXS.valeurLocale(ref), tns);
            if (wxsRef != null)
                wxsRef.ajouterReference(this);
            else
                LOG.error("R�f�rence d'�l�ment introuvable : " + ref);
        }
        if (complexType == null && simpleType == null && type != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(type));
            final WXSType wxsType = schema.resoudreReferenceType(JaxeWXS.valeurLocale(type), tns, redefine);
            if (wxsType instanceof WXSComplexType) {
                complexType = (WXSComplexType)wxsType;
                complexType.ajouterReference(this);
            } else if (wxsType instanceof WXSSimpleType)
                simpleType = (WXSSimpleType)wxsType;
        }
        if (substitutionGroup != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(substitutionGroup));
            wxsSubstitutionGroup = schema.resoudreReferenceElement(JaxeWXS.valeurLocale(substitutionGroup), tns);
            wxsSubstitutionGroup.ajouterSubstitution(this);
        }
    }
    
    public void ajouterReference(final WXSThing thing) {
        if (references == null)
            references = new ArrayList<WXSThing>();
        references.add(thing);
    }
    
    public void ajouterSubstitution(final WXSElement el) {
        if (substitutions == null)
            substitutions = new ArrayList<WXSElement>();
        substitutions.add(el);
    }
    
    public List<WXSElement> getSubstitutions() {
        return(substitutions);
    }
    
    public Set<WXSElement> listeTousElements() {
        final LinkedHashSet<WXSElement> liste = new LinkedHashSet<WXSElement>();
        liste.add(this);
        if (complexType != null)
            liste.addAll(complexType.listeTousElements());
        return(liste);
    }
    
    /**
     * El�ments nomm�s non abstraits correspondants (cet �l�ment s'il est nomm�, �l�ment nomm� si celui-ci est une r�f�rence, et substitutions).
     */
    public List<WXSElement> listeElementsCorrespondant() {
        if (correspondant != null)
            return(correspondant);
        correspondant = new ArrayList<WXSElement>();
        if (!abstractAtt && name != null)
            correspondant.add(this);
        if (wxsRef != null)
            correspondant.addAll(wxsRef.listeElementsCorrespondant());
        if (substitutions != null)
            for (WXSElement substitution : substitutions)
                correspondant.addAll(substitution.listeElementsCorrespondant());
        return(correspondant);
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        if (sousElements != null)
            return(sousElements);
        final Set<ToutElement> liste = new LinkedHashSet<ToutElement>();
        if (wxsRef != null)
            liste.addAll(wxsRef.listeSousElements());
        else if (complexType != null)
            liste.addAll(complexType.listeSousElements());
        else if (simpleType == null && type == null && wxsSubstitutionGroup != null)
            liste.addAll(wxsSubstitutionGroup.listeSousElements());
        sousElements = new ArrayList<ToutElement>(liste);
        return(sousElements);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        final Set<WXSElement> liste = new LinkedHashSet<WXSElement>();
        if (parent != null)
            liste.addAll(parent.listeElementsParents());
        if (references != null) {
            for (WXSThing reference : references) {
                if (reference instanceof WXSElement)
                    liste.addAll(((WXSElement)reference).listeElementsParents());
                else if (reference instanceof WXSAny)
                    liste.addAll(((WXSAny)reference).listeElementsParents());
            }
        }
        if (wxsSubstitutionGroup != null)
            liste.addAll(wxsSubstitutionGroup.listeElementsParents());
        return(new ArrayList<WXSElement>(liste));
    }
    
    /**
     * Expression r�guli�re pour l'interface utilisateur (avec les titres des �l�ments: ne peut pas �tre utilis�e pour la validation)
     */
    public String expressionReguliereElement() {
        // on suppose que cet �l�ment est nomm�
        if (complexType == null && simpleType == null && type == null && wxsSubstitutionGroup != null)
            return(wxsSubstitutionGroup.expressionReguliereElement());
        if (complexType == null)
            return(null);
        return(complexType.expressionReguliere());
    }
    
    /**
     * E.R. pour cet �l�ment comme sous-�l�ment d'un mod�le.
     * Renvoie null s'il n'y a pas d'�l�ment non-abstrait correspondant � cet �l�ment.
     */
    public String expressionReguliere() {
        final List<WXSElement> liste = listeElementsCorrespondant();
        if (liste.size() == 0)
            return(null);
        final StringBuilder sb = new StringBuilder();
        if (liste.size() > 1)
            sb.append('(');
        for (Iterator<WXSElement> iter = liste.iterator(); iter.hasNext(); ) {
            sb.append(schema.titreElement((iter.next()).getDOMElement()));
            if (iter.hasNext())
                sb.append('|');
        }
        if (liste.size() > 1)
            sb.append(')');
        if (minOccurs == 0 && maxOccurs == 1)
            sb.append('?');
        else if (minOccurs == 0 && maxOccurs > 1)
            sb.append('*');
        else if (minOccurs > 0 && maxOccurs > 1)
            sb.append('+');
        return(sb.toString());
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        // on suppose que cet �l�ment est nomm�
        if (complexType == null && simpleType == null && type == null && wxsSubstitutionGroup != null)
            return(wxsSubstitutionGroup.enfantObligatoire(enfant));
        if (complexType == null)
            return(null);
        return(complexType.enfantObligatoire(enfant));
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        // on suppose que cet �l�ment est nomm�
        if (complexType == null && simpleType == null && type == null && wxsSubstitutionGroup != null)
            return(wxsSubstitutionGroup.enfantsMultiples(enfant));
        if (complexType == null)
            return(null);
        return(complexType.enfantsMultiples(enfant));
    }
    
    public ArrayList<String> listeValeurs() {
        if (fixed != null) {
            final ArrayList<String> fixedval = new ArrayList<String>();
            fixedval.add(fixed);
            return(fixedval);
        }
        if (simpleType != null)
            return(simpleType.listeValeurs());
        else if (complexType != null)
            return(complexType.listeValeurs());
        else if (type != null)
            return(JaxeWXS.listeValeursBooleen(type, domElement));
        else if (simpleType == null && wxsSubstitutionGroup != null)
            return(wxsSubstitutionGroup.listeValeurs());
        return(null);
    }
    
    public ArrayList<WXSAttribute> listeAttributs() {
        if (wxsRef != null)
            return(wxsRef.listeAttributs());
        if (complexType != null)
            return(complexType.listeAttributs());
        else if (simpleType == null && type == null && wxsSubstitutionGroup != null)
            return(wxsSubstitutionGroup.listeAttributs());
        return(new ArrayList<WXSAttribute>());
    }
    
    public boolean contientDuTexte() {
        if (type != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(type));
            final String schemaNamespace = domElement.getNamespaceURI();
            // si le type fait partie des sch�mas XML (comme "string" ou "anyURI")
            // on consid�re que c'est du texte (sauf si le sch�ma est le sch�ma des sch�mas)
            if (!schemaNamespace.equals(schema.getTargetNamespace()) && schemaNamespace.equals(tns))
                return(true);
        }
        if (complexType != null) {
            if (complexType.getMixed())
                return(true);
            if (complexType.getSimpleContent() != null)
                return(true);
        }
        if (simpleType != null)
            return(true);
        if (complexType == null && type == null && wxsSubstitutionGroup != null)
            return(wxsSubstitutionGroup.contientDuTexte());
        return(false);
    }
    
    /**
     * Validation d'un �l�ment nomm�.
     * Renvoie true si la liste de sous-�l�ments pass�e en param�tre est un ensemble de sous-�l�ments valide.
     * Si insertion est true, tous les sous-�l�ments sont optionnels.
     */
    public boolean validerSousElements(final List<WXSElement> sousElements, final boolean insertion) {
        if (complexType == null) {
            if (simpleType == null && type == null && wxsSubstitutionGroup != null)
                return(wxsSubstitutionGroup.validerSousElements(sousElements, insertion));
            return(sousElements.size() == 0);
        }
        if (sousElements.size() == 0) {
            if (insertion)
                return(true);
            if (complexType.estOptionnel())
                return(true);
        }
        final int pos = complexType.valider(sousElements, 0, insertion);
        return(pos > 0 && pos == sousElements.size());
    }
    
    /**
     * validation d'un sous-�l�ment
     * renvoie la position dans la liste jusqu'o� la validation est possible (start si aucune validation possible, sousElements().size() si tout est valid�)
     */
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        int nb = 0;
        final List<WXSElement> correspondant = listeElementsCorrespondant();
        for (int i=start; i<sousElements.size(); i++) {
            if (nb >= maxOccurs)
                return(i);
            boolean trouve = false;
            for (WXSElement el : correspondant)
                if (el == sousElements.get(i))
                    trouve = true;
            if (!trouve) {
                if (!insertion && nb < minOccurs)
                    return(start);
                return(i);
            }
            nb++;
        }
        if (!insertion && nb < minOccurs)
            return(start);
        return(start + nb);
    }
    
    public boolean estOptionnel() {
        return(minOccurs == 0);
    }
    
    public boolean validerValeur(final String valeur) {
        if (fixed != null)
            return(fixed.equals(valeur));
        if (simpleType != null)
            return(simpleType.validerValeur(valeur));
        if (complexType != null)
            return(complexType.validerValeur(valeur));
        if (type != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(type));
            if (tns != null && tns.equals(domElement.getNamespaceURI()))
                return(WXSSimpleType.validerValeur(JaxeWXS.valeurLocale(type), valeur));
            return(false);
        } else
            return(true);
    }
}
