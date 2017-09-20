package com.sufnom;

import com.sufnom.lib.ParameterFilter;
import com.sufnom.node.Node;
import com.sufnom.node.NodeTerminal;
import com.sufnom.node.Synapse;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

public class NodeInterface {
    private static final int PORT = 1997;

    private static final String MSG_SERVER_STARTED = "Server Started";

    private static final String CONEXT_AUTH = "/auth";
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
                    case CONEXT_AUTH:
                        handleAuthResponse(t, postMap);
                        break;
                    default: sendResponse(t, 200, null);
                }
            }
            catch (Exception e){
                e.printStackTrace();
                sendResponse(t, 200, "error");
            }
        }

        private void handleAuthResponse(HttpExchange t, Map postMap) throws Exception{
            try {
                String email = (String) postMap.get("email");
                String password = (String)postMap.get("password");
                sendResponse(t, 200, NodeTerminal.getSession().signIn(email, password));
            }
            catch (Exception e){
                e.printStackTrace();
                sendResponse(t,500, "error");
            }
        }

        private void handleNodeResponse(HttpExchange t, Map postMap) throws Exception{
            String request = (String)postMap.get(REQUEST);
            if (request == null){
                sendResponse(t, 200, null);
                return;
            }
            getSessionAdmin(postMap);
            switch (request){
                case REQUEST_LIST:
                    sendResponse(t, 200,
                            NodeTerminal.getSession().getFactory()
                                    .getNodeList(Long.parseLong((String)postMap
                                            .get("parent"))).toString());
                    break;
                case REQUEST_INSERT:
                    JSONObject ob = new JSONObject((String)postMap.get("node"));
                    JSONObject content = ob.getJSONObject("content");
                    Node node = NodeTerminal.getSession().getFactory()
                            .insertNode(
                                    Long.parseLong((String)postMap.get("parent")),
                                    content.toString(),
                                    getSessionAdmin(postMap));
                    if (node != null)
                        sendResponse(t, 200, node.toString());
                    else sendResponse(t, 500, "");
                    break;
            }
        }

        private long getSessionAdmin(Map postMap) throws Exception{
            return NodeTerminal.getSession().getAdmin((String)postMap.get("session"));
        }

        private void sendResponse(HttpExchange t, int status, String response){
            try {
                if (response == null){
                    JSONObject ob = new JSONObject();
                    ob.put("Status", "Server OK");
                    response = ob.toString();
                }
                byte[] rawResponse = response.getBytes();
                System.out.println("Response : " + response);
                t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                t.getResponseHeaders().set("Content-Type", "text/plain");
                t.sendResponseHeaders(status, rawResponse.length);
                OutputStream os = t.getResponseBody();
                os.write(rawResponse);
                os.close();
            }
            catch (Exception e){e.printStackTrace();}
        }

        private void handleSynapseResponse(HttpExchange t, Map postMap) throws Exception{
            String request = (String)postMap.get(REQUEST);
            getSessionAdmin(postMap);
            switch (request){
                case REQUEST_LIST:
                    sendResponse(t, 200,
                            NodeTerminal.getSession().getFactory()
                                    .getSynapseList(Long.parseLong((String)postMap
                                            .get("node"))).toString());
                    break;
                case REQUEST_INSERT:
                    JSONObject ob = new JSONObject((String)postMap.get("synapse"));
                    Synapse synapse = NodeTerminal.getSession().getFactory()
                            .insertSynapse(
                                    Long.parseLong((String)postMap.get("node")),
                                    ob.getJSONObject("content").toString(),
                                    getSessionAdmin(postMap));
                    if (synapse != null)
                        sendResponse(t, 200, synapse.toString());
                    else sendResponse(t, 500, "");
                    break;
            }
        }
    }
}
