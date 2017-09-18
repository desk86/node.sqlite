package com.sufnom.node;

import org.json.JSONObject;

import java.sql.ResultSet;

public class Node {
    private static final String KEY_NODE_ID = "id";
    private static final String KEY_NODE_CONTENT = "content";

    public final long nodeId;

    private JSONObject content;

    public Node(long nodeId){
        this.nodeId = nodeId;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public JSONObject getJSON(){
        JSONObject ob = new JSONObject();
        try {
            ob.put(KEY_NODE_ID, nodeId);
            ob.put(KEY_NODE_CONTENT, content);
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
            node.setContent(new JSONObject(rs.getString(3)));
            return node;
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }
}
