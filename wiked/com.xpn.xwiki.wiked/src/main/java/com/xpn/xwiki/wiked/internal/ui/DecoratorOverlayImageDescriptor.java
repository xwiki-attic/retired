
package com.xpn.xwiki.wiked.internal.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class DecoratorOverlayImageDescriptor extends CompositeImageDescriptor {

	/** the base image */
	private Image base;
	/** the overlay image */
	private ImageDescriptor overlay;
	/** the size */
	private Point size;
    /** Placement */
    private int placement;
    
    public static final int BOTTOM_LEFT = 1;
    public static final int BOTTOM_RIGHT = 2;

    public DecoratorOverlayImageDescriptor(Image base, ImageDescriptor overlay) {
    	this(base, overlay, null, BOTTOM_RIGHT);
    }
    
    public DecoratorOverlayImageDescriptor(Image base, ImageDescriptor overlay,
        int placement) {
        this(base, overlay, null, placement);
    }

    public DecoratorOverlayImageDescriptor(Image base, ImageDescriptor overlay,
		Point size, int placement) {
    	if (base == null) {
    		throw new IllegalArgumentException("no base");
    	}
		this.base = base;
    	if (overlay == null) {
    		throw new IllegalArgumentException("no overlay");
    	}
		this.overlay = overlay;
		this.size = (size != null) ? size : new Point(16,16);
        this.placement = placement;
	}

	protected void drawOverlay() {
		ImageData overlayData = overlay.getImageData();
		if (overlayData == null) {
			overlayData = ImageDescriptor.getMissingImageDescriptor().getImageData();
        }
        switch (this.placement) {
            case BOTTOM_LEFT:
                drawImage(overlayData, 0, size.y - overlayData.height);
                break;
            case BOTTOM_RIGHT:
                drawImage(overlayData, size.x - overlayData.width, 
                    size.y - overlayData.height);
                break;
        }
	}

	protected void drawCompositeImage(int width, int height) {
		drawImage(base.getImageData(), 0, 0);
		drawOverlay();
	}

	protected Point getSize() {
		return size;
	}
}