package org.xwiki.xdomexplorer.editors;

import org.eclipse.jface.viewers.LabelProvider;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.WordBlock;

public class XDOMLabelProvider extends LabelProvider
{
    private String truncate(String string, int maxLength)
    {
        if (string.length() > maxLength) {
            return String.format("%s...", string.substring(0, maxLength));
        }

        return string;
    }

    @Override
    public String getText(Object element)
    {
        if (element instanceof WordBlock) {
            WordBlock wordBlock = (WordBlock) element;
            return String.format("WordBlock [%s]", truncate(wordBlock.getWord(), 15));
        }
        
        if (element instanceof MacroBlock) {
            MacroBlock macroBlock = (MacroBlock) element;
            return String.format("MacroBlock [%s]", truncate(macroBlock.getContent(), 15));
        }

        String className = element.getClass().getName(); 
        
        int index = className.lastIndexOf('.');
        if(index == -1) {
            index = 0;
        }
        else {
            index++;
        }
        
        return className.substring(index, className.length());
    }

}
