

package jaxe.wxs.copy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public abstract class WXSAnnotated implements WXSThing {
    
    protected WXSAnnotation annotation = null;
    
    
    public WXSAnnotation getAnnotation() {
        return(annotation);
    }
    
    protected void parseAnnotation(final Element el) {
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element && "annotation".equals(n.getLocalName())) {
                annotation = new WXSAnnotation((Element)n);
                break;
            }
        }
    }
    
    public String getDocumentation() {
        if (annotation == null)
            return(null);
        return(annotation.getDocumentation());
    }
}
