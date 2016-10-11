package org.hpccsystems.eclBuilder.controller;

import java.util.LinkedList;

import org.hpccsystems.eclBuilder.domain.FileMeta;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class FileMetaTreeNode extends DefaultTreeNode<org.hpccsystems.eclBuilder.domain.FileMeta> {
	    private static final long serialVersionUID = 1L;
	    int count;
	    boolean leaf;
	    
		public FileMetaTreeNode(org.hpccsystems.eclBuilder.domain.FileMeta category) {
	        super(category, new LinkedList<TreeNode<FileMeta>>()); // assume not a leaf-node
	        this.count = 0;
	    }
	 
	    public FileMetaTreeNode(FileMeta category, int count) {
	        super(category, new LinkedList<TreeNode<FileMeta>>()); // assume not a leaf-node
	        this.count = count;
	    }
	    
	    public FileMetaTreeNode(FileMeta obj, FileMetaTreeNode[] obj1){
	    	super(obj, obj1);
	    }
	 
	    public String getDescription() {
	        return getData().getFileName();
	    }
	 
	    public int getCount() {
	        return count;
	    }
	 
	    public void setLeaf(boolean leaf) {
			this.leaf = leaf;
		}

	    public boolean isLeaf() {
	        return getData() != null;
	    }
	}

