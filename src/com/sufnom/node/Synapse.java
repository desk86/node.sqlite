package com.sufnom.node;

import org.json.JSONObject;

import java.sql.ResultSet;

public class Synapse {
    private static final String KEY_SYNAPSE_ID = "id";
    private static final String KEY_CONTENT = "content";

    public final long synapseId;

    private JSONObject content;
    private long editorId;
    private long parentId;
    private long timeStamp;

    public Synapse(long synapseId){ this.synapseId = synapseId; }

    public long getEditorId() { return editorId; }
    public void setEditorId(long editorId) { this.editorId = editorId; }

    public long getParentId() { return parentId; }
    public void setParentId(long parentId) { this.parentId = parentId; }

    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

    public JSONObject getContent() { return content; }
    public void setContent(JSONObject content) { this.content = content; }

    public JSONObject getJSON(){
        JSONObject ob = new JSONObject();
        try {
            ob.put(KEY_SYNAPSE_ID, synapseId);
            ob.put(KEY_CONTENT, content);
            ob.put("editor", Editor.getEditorInfo(editorId));
            ob.put("parent", parentId);
            ob.put("timestamp", timeStamp);
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

    public static Synapse getFrom(ResultSet rs){
        try {
            Synapse synapse = new Synapse(rs.getLong(1));
            synapse.setEditorId(rs.getLong(2));
            synapse.setParentId(rs.getLong(3));
            synapse.setContent(new JSONObject(rs.getString(4)));
            synapse.setTimeStamp(rs.getLong(5));
            return synapse;
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }
}
