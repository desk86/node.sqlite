package com.sufnom.node;

import org.json.JSONObject;

public class Synapse {
    private static final String KEY_NODE_ID = "id";
    private static final String KEY_NODE_CONTENT = "content";

    public final long synapseId;

    private String content;

    public Synapse(long synapseId){ this.synapseId = synapseId; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public JSONObject getJSON(){
        JSONObject ob = new JSONObject();
        try {
            ob.put(KEY_NODE_ID, synapseId);
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
}
