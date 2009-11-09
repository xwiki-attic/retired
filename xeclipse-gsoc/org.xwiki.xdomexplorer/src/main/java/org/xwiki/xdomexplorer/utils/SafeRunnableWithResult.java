package org.xwiki.xdomexplorer.utils;

import org.eclipse.jface.util.SafeRunnable;

public abstract class SafeRunnableWithResult<T> extends SafeRunnable
{
        private T result = null;
                  
        public T getResult()
        {
            return result;
        }

        public void setResult(T result)
        {
            this.result = result;            
        }
}
