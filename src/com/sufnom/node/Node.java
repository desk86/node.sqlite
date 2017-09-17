package com.sufnom.node;

import org.json.JSONObject;

import java.sql.ResultSet;

public class Node {
    private static final String KEY_NODE_ID = "id";
    private static final String KEY_NODE_CONTENT = "content";

    public final long nodeId;

    private String nodeTitle;

    public Node(long nodeId){
        this.nodeId = nodeId;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    public JSONObject getJSON(){
        JSONObject ob = new JSONObject();
        try {
            ob.put(KEY_NODE_ID, nodeId);
            ob.put(KEY_NODE_CONTENT, nodeTitle);
        }
        catch (Exception e){e.printStackTrace();}
        return ob;
    }

    @Override
    public String toString() {
        try {
            return getJSON().toString();
        }
        catch (Exception e){e.printStackTrace();}
        return super.toString();
    }

    public static Node getFrom(ResultSet rs){
        try {
            Node node = new Node(rs.getLong(1));
            node.setNodeTitle(rs.getString(2));
            return node;
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }
}
