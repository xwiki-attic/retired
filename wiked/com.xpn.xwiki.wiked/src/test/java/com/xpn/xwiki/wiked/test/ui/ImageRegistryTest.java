
package com.xpn.xwiki.wiked.test.ui;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageRegistry;

import com.xpn.xwiki.wiked.internal.WikedPlugin;
import com.xpn.xwiki.wiked.internal.ui.WikedImageRegistry;

/**
 * 
 * @author psenicka_ja
 */
public class ImageRegistryTest extends TestCase {

    private ImageRegistry images;
    
	protected void setUp() throws Exception {
		this.images = WikedPlugin.getInstance().getImageRegistry();
	}
    
    public void testImage() throws Exception {
         assertNotNull(images.get(WikedImageRegistry.REPOSITORY).getImageData());
         assertNotNull(images.get(WikedImageRegistry.SPACE).getImageData());
         assertNotNull(images.get(WikedImageRegistry.PAGE).getImageData());
    }

}
