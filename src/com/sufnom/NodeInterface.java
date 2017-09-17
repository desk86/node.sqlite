package com.sufnom;

import com.sufnom.lib.ParameterFilter;
import com.sufnom.node.Node;
import com.sufnom.node.NodeTerminal;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.omg.CORBA.TRANSACTION_MODE;

import java.awt.*;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

public class NodeInterface {
    private static final int PORT = 1997;

    private static final String MSG_SERVER_STARTED = "Server Started";

    private static final String CONTEXT_NODE = "/node";
    private static final String CONTEXT_SYNAPSE = "/synapse";

    private static final String REQUEST = "request";
    private static final String REQUEST_LIST = "list";
    private static final String REQUEST_INSERT = "insert";

    public static void main(String[] args) {
	    // write your code here
        NodeInterface nodeInterface = new NodeInterface();
        try {
            NodeTerminal.getSession().getFactory().connect();
            nodeInterface.onHttpStart();
        }
        catch (Exception e){e.printStackTrace();}
    }

    private void onHttpStart() throws Exception{
        System.out.println("Listening to : " + PORT);
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT),0);
        HttpContext context = server.createContext("/", new DefaultHandler());
        context.getFilters().add(new ParameterFilter());
        server.setExecutor(null);
        server.start();
        System.out.println(MSG_SERVER_STARTED);
    }

    static class DefaultHandler implements HttpHandler{
        public void handle(HttpExchange t){
            try {
                String path = t.getRequestURI().getPath();
                Map postMap = (Map)t.getAttribute("parameters");
                switch (path){
                    case CONTEXT_NODE:
                        handleNodeResponse(t, postMap);
                        break;
                    case CONTEXT_SYNAPSE:
                        handleSynapseResponse(t, postMap);
                        break;
                }
            }
            catch (Exception e){e.printStackTrace();}
        }

        private void handleNodeResponse(HttpExchange t, Map postMap) throws Exception{
            String request = (String)postMap.get(REQUEST);
            switch (request){
                case REQUEST_LIST:
                    sendResponse(t, 200,
                            NodeTerminal.getSession().getFactory()
                                    .getNodeList(Long.parseLong((String)postMap
                                            .get("parent"))).toString());
                    break;
                case REQUEST_INSERT:
                    JSONObject ob = new JSONObject((String)postMap.get("node"));
                    Node node = NodeTerminal.getSession().getFactory()
                            .insertNew(Long.parseLong((String)postMap.get("parent")),
                                    ob.getString("content"));
                    if (node != null)
                        sendResponse(t, 200, node.toString());
                    else sendResponse(t, 500, "");
                    break;
            }
        }

        private void sendResponse(HttpExchange t, int status, String response){
            byte[] rawResponse = response.getBytes();
            try {
                t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                t.getResponseHeaders().set("Content-Type", "text/plain");
                t.sendResponseHeaders(status, rawResponse.length);
                OutputStream os = t.getResponseBody();
                os.write(rawResponse);
                os.close();
            }
            catch (Exception e){e.printStackTrace();}
        }

        private void handleSynapseResponse(HttpExchange t, Map postMap){

        }
    }
}
