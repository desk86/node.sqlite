package com.sufnom.node;

import com.sun.org.apache.xalan.internal.lib.ExsltBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NodeFactory {
    private Connection connection;

    public Editor getEditor(String email, String password){
        try {
            String query = "select * from editor where email = ? and pass = ? limit 1";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            Editor editor = null;
            if (rs.next()){
                editor = Editor.getFrom(rs);
            }
            rs.close();
            statement.close();
            return editor;
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }

    public Synapse insertSynapse(long nodeId, String content, long adminId){
        try {
            String query = "insert into synapse(admin,parent,content,timestamp) values (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, adminId);
            statement.setLong(2, nodeId);
            statement.setString(3, content);
            statement.setLong(4, System.currentTimeMillis());
            int affectedRows = statement.executeUpdate();
            connection.commit();

            ResultSet rs = statement.getGeneratedKeys();
            long id = 0;
            if (rs.next()){
                id = rs.getLong(1);
            }
            rs.close();
            statement.close();
            if (id == 0){
                System.out.println("Synapse not inserted");
                return null;
            }
            else return getSynapse(id);
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }

    public Node insertNode(long parentId, String content, long adminId){
        try {
            String query = "insert into node(content, admin, editors) values (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, content);
            statement.setLong(2, adminId);
            statement.setString(3, "[" + adminId + "]");
            int affectedRows = statement.executeUpdate();
            connection.commit();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                Node node = new Node(rs.getLong(1));
                node.setContent(new JSONObject(content));
                rs.close();
                addRelation(parentId, node.nodeId);
                return node;
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public Synapse getSynapse(long synapseId){
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "select * from synapse where id = ?");
            statement.setLong(1, synapseId);
            ResultSet rs = statement.executeQuery();
            Synapse synapse = null;
            if (rs.next()){
                synapse = Synapse.getFrom(rs);
            }
            rs.close();
            statement.close();
            return synapse;
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }

    private void addRelation(long parentId, long childId){
        try {
            String query = "insert into map(parent, child) values (?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, parentId);
            statement.setLong(2, childId);
            statement.executeUpdate();
            statement.close();
            connection.commit();
        }
        catch (Exception e){e.printStackTrace();}
    }

    private List<Long> getChildList(long parentId){
        List<Long> childIdList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String sql = "select * from map where parent = '" + parentId + "'" ;
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                childIdList.add(rs.getLong(2));
            }
            rs.close();
            statement.close();
        }
        catch (Exception e){e.printStackTrace();}
        return childIdList;
    }

    public JSONArray getNodeList(long parentId){
        JSONArray array = new JSONArray();
        Node node;
        try {
            Statement statement = connection.createStatement();
            for (long childId : getChildList(parentId)){
                String sql = "select * from node where id = '" + childId + "'";
                ResultSet rs = statement.executeQuery(sql);
                if (rs.next()){
                    node = Node.getFrom(rs);
                    if (node != null)
                        array.put(node.getJSON());
                }
                rs.close();
            }
            statement.close();
        }
        catch (Exception e){e.printStackTrace();}
        return array;
    }

    public JSONArray getSynapseList(long nodeId){
        JSONArray array = new JSONArray();
        try {
            Statement statement = connection.createStatement();
            String sql = "select * from synapse where parent = '" + nodeId + "'" ;
            ResultSet rs = statement.executeQuery(sql);
            Synapse synapse;
            while (rs.next()){
                synapse = Synapse.getFrom(rs);
                if (synapse != null)
                    array.put(synapse.getJSON());
            }
            rs.close();
            statement.close();
        }
        catch (Exception e){e.printStackTrace();}
        return array;
    }

    public Editor getEditor(long editorId){
        try {
            Statement statement = connection.createStatement();
            String sql = "select * from editor where " +
                    "id = '" + editorId + "'";
            ResultSet rs = statement.executeQuery(sql);
            Editor editor = null;
            if (rs.next())
                editor = Editor.getFrom(rs);
            else System.out.println(sql);
            rs.close();
            statement.close();
            return editor;
        }
        catch (Exception e){e.printStackTrace();}
        return null;
    }

    public void connect(){
        try {
            String url = "jdbc:sqlite:F:/node-ext.db";
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);
        }
        catch (Exception e){e.printStackTrace();}
    }

    public void disConnect(){
        try {
            connection.close();
        }
        catch (Exception e){e.printStackTrace();}
    }
}
