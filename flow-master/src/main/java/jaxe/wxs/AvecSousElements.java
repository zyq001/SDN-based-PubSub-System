/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform?ment aux dispositions de la Licence Publique G?n?rale GNU, telle que publi?e par la Free Software Foundation ; version 2 de la licence, ou encore (? votre choix) toute version ult?rieure.

Ce programme est distribu? dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m?me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d?tail, voir la Licence Publique G?n?rale GNU .

Vous devez avoir re?u un exemplaire de la Licence Publique G?n?rale GNU en m?me temps que ce programme ; si ce n'est pas le cas, ?crivez ? la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Repr?sente un ?l?ment des sch?mas XML qui peut contenir des sous-?l?ments ou pointer vers des sous-?l?ments (?l?ments XML autoris?s sous un ?l?ment).
 */
public interface AvecSousElements {
    
    /**
     * Resoudre les r?f?rences vers d'autres ?l?ments du sch?ma de tous les descendants de cet ?l?ment du sch?ma.
     */
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine);
    
    /**
     * Renvoie la liste des ?l?ments d?finis sous ce mod?le (parmi tous les descendants de l'?l?ment des sch?mas, mais sans les r?f?rences d'?l?ments).
     */
    public Set<WXSElement> listeTousElements();
    
    /**
     * Renvoie la liste des tous les ?l?ments que l'on peut trouver directement sous ce mod?le (y compris avec des r?f?rences).
     */
    public ArrayList<ToutElement> listeSousElements();
    
    /**
     * Renvoie une expression r?guli?re correspondant ? ce mod?le, destin?e ? ?tre lue par un utilisateur (mais pas utilisable pour autre chose).
     */
    public String expressionReguliere();
    
    /**
     * Renvoie Boolean.TRUE si l'?l?ment est un enfant obligatoire dans ce mod?le.
     * Renvoie null si l'enfant n'en est pas un.
     */
    public Boolean enfantObligatoire(final WXSElement enfant);
    
    /**
     * Renvoie Boolean.TRUE si l'?l?ment pass? en param?tre peut se retrouver plusieurs fois de suite dans ce mod?le ? cause d'un maxOccurs > 1.
     * Renvoie null si l'enfant n'en est pas un.
     */
    public Boolean enfantsMultiples(final WXSElement enfant);
    
    /**
     * renvoie la position dans la liste jusqu'o? la validation est possible (0 si aucune validation possible, sousElements().size() si tout est valid?)
     */
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion);
    
    /**
     * Renvoie true si l'ensemble des ?l?ments correspondant est optionnel, par exemple pour une s?quence avec uniquement des ?l?ments optionnels.
     */
    public boolean estOptionnel();
    
}
