package com.sufnom.node;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NodeFactory {
    private Connection connection;

    public Node insertNew(long parentId, String content){
        try {
            String query = "insert into node(content) values (?)";
            PreparedStatement statement = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, content);
            int affectedRows = statement.executeUpdate();
            connection.commit();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                Node node = new Node(rs.getLong(1));
                node.setNodeTitle(content);
                rs.close();
                addRelation(parentId, node.nodeId);
                return node;
            }
        }catch (Exception e){e.printStackTrace();}
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
        }
        catch (Exception e){e.printStackTrace();}
        return array;
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
