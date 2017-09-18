package com.sufnom.node;

import org.json.JSONObject;

import java.sql.ResultSet;

public class Editor {
    public final long editorId;

    private String name;
    public Editor(long editorId){this.editorId = editorId;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JSONObject getOb(){
        JSONObject object = new JSONObject();
        try {
            object.put("id", editorId);
            object.put("name", name);
        }
        catch (Exception e){e.printStackTrace();}
        return object;
    }

    @Override
    public String toString() {
        return getOb().toString();
    }

    public static Editor getFrom(ResultSet rs){
        try {
            Editor editor = new Editor(rs.getLong(1));
            editor.setName(rs.getString(3));
            return editor;
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }

    public static JSONObject getEditorInfo(long editorId){
        Editor editor = NodeTerminal.getSession().getFactory()
                .getEditor(editorId);
        if (editor != null)
            return editor.getOb();
        return new JSONObject();
    }
}
