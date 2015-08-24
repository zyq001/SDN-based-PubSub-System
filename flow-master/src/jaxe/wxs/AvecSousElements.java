/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Représente un élément des schémas XML qui peut contenir des sous-éléments ou pointer vers des sous-éléments (éléments XML autorisés sous un élément).
 */
public interface AvecSousElements {
    
    /**
     * Resoudre les références vers d'autres éléments du schéma de tous les descendants de cet élément du schéma.
     */
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine);
    
    /**
     * Renvoie la liste des éléments définis sous ce modèle (parmi tous les descendants de l'élément des schémas, mais sans les références d'éléments).
     */
    public Set<WXSElement> listeTousElements();
    
    /**
     * Renvoie la liste des tous les éléments que l'on peut trouver directement sous ce modèle (y compris avec des références).
     */
    public ArrayList<ToutElement> listeSousElements();
    
    /**
     * Renvoie une expression régulière correspondant à ce modèle, destinée à être lue par un utilisateur (mais pas utilisable pour autre chose).
     */
    public String expressionReguliere();
    
    /**
     * Renvoie Boolean.TRUE si l'élément est un enfant obligatoire dans ce modèle.
     * Renvoie null si l'enfant n'en est pas un.
     */
    public Boolean enfantObligatoire(final WXSElement enfant);
    
    /**
     * Renvoie Boolean.TRUE si l'élément passé en paramètre peut se retrouver plusieurs fois de suite dans ce modèle à cause d'un maxOccurs > 1.
     * Renvoie null si l'enfant n'en est pas un.
     */
    public Boolean enfantsMultiples(final WXSElement enfant);
    
    /**
     * renvoie la position dans la liste jusqu'où la validation est possible (0 si aucune validation possible, sousElements().size() si tout est validé)
     */
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion);
    
    /**
     * Renvoie true si l'ensemble des éléments correspondant est optionnel, par exemple pour une séquence avec uniquement des éléments optionnels.
     */
    public boolean estOptionnel();
    
}
