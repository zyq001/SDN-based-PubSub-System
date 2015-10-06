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

import org.w3c.dom.Element;


public class WXSSequence extends WXSExplicitGroup {
    
    public WXSSequence(final Element el, final Parent parent, final WXSSchema schema) {
        parse(el, parent, schema);
    }
    
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        int nb = 0;
        for (int i=start; i<sousElements.size(); ) {
            if (nb >= maxOccurs)
                return(i);
            int pos = i;
            for (AvecSousElements nestedParticle : nestedParticles) {
                int pos2 = nestedParticle.valider(sousElements, pos, insertion);
                if (pos2 == pos) {
                    if (!insertion && !nestedParticle.estOptionnel()) {
                        if (nb < minOccurs)
                            return(start);
                        return(i);
                    }
                }
                pos = pos2;
            }
            if (pos == i)
                return(i);
            i = pos;
            nb++;
        }
        if (!insertion && nb < minOccurs)
            return(start);
        return(sousElements.size());
    }
    
    public boolean estOptionnel() {
        if (minOccurs == 0)
            return(true);
        for (AvecSousElements nestedParticle: nestedParticles) {
            if (!nestedParticle.estOptionnel())
                return(false);
        }
        return(true);
    }
    
}
