package com.sufnom.node;

public class NodeTerminal {
    private static NodeTerminal session = new NodeTerminal();
    public static NodeTerminal getSession() {
        return session;
    }

    private NodeFactory factory = new NodeFactory();

    public NodeFactory getFactory() {
        return factory;
    }
}
