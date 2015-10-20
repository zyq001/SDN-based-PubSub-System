/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform閙ent aux dispositions de la Licence Publique G閚閞ale GNU, telle que publi閑 par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult閞ieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m阭e la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d閠ail, voir la Licence Publique G閚閞ale GNU .

Vous devez avoir re鐄 un exemplaire de la Licence Publique G閚閞ale GNU en m阭e temps que ce programme ; si ce n'est pas le cas, 閏rivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import org.w3c.dom.Element;


public class WXSKeyref extends WXSKeybase {
    
    protected String refer = null;
    
    
    public WXSKeyref(final Element el) {
        parse(el);
        
    }
    
}
