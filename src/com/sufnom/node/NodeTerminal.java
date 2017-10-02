package com.sufnom.node;

import com.sufnom.lib.LRUCache;
import com.sufnom.lib.ZedBase64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

public class NodeTerminal {
    private static NodeTerminal session = new NodeTerminal();
    public static NodeTerminal getSession() {
        return session;
    }

    private LRUCache<String, Long> sessionCache = new LRUCache<>(100);
    private NodeFactory factory = new NodeFactory();

    public NodeFactory getFactory() {
        return factory;
    }
    public String signIn(String email, String password){
        Editor editor = factory.getEditor(email, password);
        if (editor == null) return "null";
        else {
            JSONObject ob = new JSONObject();
            try {
                ob.put("session", pushSession(editor.editorId));
                ob.put("node", editor.getRootNodeId());
            }
            catch (Exception e){e.printStackTrace();}
            return ob.toString();
        }
    }

    public String pushSession(long userId){
        String uid = UUID.randomUUID().toString();
        sessionCache.put(uid, userId);
        return uid;
    }

    public long getAdmin(String sessionId) throws Exception{
        long id = sessionCache.get(sessionId);
        if (id == 0) throw new Exception("Invalid Session");
        return id;
    }

    public static String readFile(String filePath){
        try {
            InputStream stream = new FileInputStream(new File(filePath));
            return new String(IOUtils.toByteArray(stream));
        }
        catch (Exception e){e.printStackTrace();}
        return "";
    }
}
