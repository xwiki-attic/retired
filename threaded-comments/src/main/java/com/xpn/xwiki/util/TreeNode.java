/**
 * ===================================================================
 *
 * Copyright (c) 2003 Ludovic Dubost, All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details, published at
 * http://www.gnu.org/copyleft/gpl.html or in gpl.txt in the
 * root folder of this distribution.
 *
 * Created by
 * User: Christophe Clermont, Stéphane Laurière cclermont|slauriere@mandriva.com
 * Date: August 2005
 * Time: 17:04:20
 */
package com.xpn.xwiki.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xpn.xwiki.api.Object;

public class TreeNode {
    private Object object;
    private List children;
    private TreeNode parent;
        
    public TreeNode() {
        object = null;
        children = new ArrayList();
        parent = null;
    }
    
    public TreeNode(Object bo, TreeNode pa) {
        this();
        object = bo;
        parent = pa;
    }
    
    public int getDepth() {
        if (parent != null)
            return 1 + parent.getDepth();
        return 0;
    }
    
    public Object getObject() {
        return object;
    }
    public void setObject(Object obj) {
        object = obj;
    }
    
    public List getChildren() {
        return children;
    }
    public void setChildren(List list) {
        children = list;
    }
    
    public TreeNode getParent() {
        return parent;
    }
    public void setParent(TreeNode tn) {
        parent = tn;
    }
    
    public int size() {
        int nodeSize = 0;
        if (object != null)
            nodeSize = 1;
        Iterator it = children.iterator();
        while (it.hasNext()) {
            TreeNode child = (TreeNode) it.next();
            nodeSize += child.size();
        }
        return nodeSize;
    }
    
    public boolean equals(TreeNode tn) {
        return object.equals(tn.getObject()) && children.equals(tn.getChildren());
    }
    
    public boolean equals(Object obj) {
        return false;
    }
    
    public void insert(List elements, String property) {
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            Object obj = (Object) it.next();
            if (obj != null)
                if (obj.getXWikiObject().getIntValue(property) == -1)
                    children.add(new TreeNode(obj, this));
                else {
                    boolean result = false;
                    Iterator itChilds = children.iterator();
                    while (itChilds.hasNext() && !result) {
                        TreeNode child = (TreeNode) itChilds.next();
                        if (child.insert(obj, property))
                            result = true;
                    }
                    if (!result)
                        children.add(new TreeNode(obj, this));
                }
        }
    }
    
    public boolean insert(Object obj, String property) {
        if (object != null) {
            if (obj.getXWikiObject().getIntValue(property) == object.getNumber()) {
                children.add(new TreeNode(obj, this));
                return true;
            }
            Iterator it = children.iterator();
            while (it.hasNext()) {
                TreeNode child = (TreeNode) it.next();
                if (child.insert(obj, property))
                    return true;
            }
        }
                
        return false;
    }
}
