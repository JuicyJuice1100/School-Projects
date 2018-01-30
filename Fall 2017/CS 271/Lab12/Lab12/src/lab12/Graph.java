package lab12;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;

/**
 *
 * @author Justin Espiritu
 */
public class Graph{
    private boolean isDirected;
    private int edgeCount = 0, vertexCount = 0;
    private HashMap<Vertex, ArrayList<Edge>> map;
    
    /**
     * Constructor
     * @param isDirected 
     */
    public Graph(boolean isDirected){
        this.isDirected = isDirected;
        map = new HashMap<>();
    }
    
    
    /**
     * insert new vertex to hashmap if it doesn't exist already
     * @param vertex
     * @return 
     */
    public boolean insertVertex(Vertex vertex){
        if(map.containsKey(vertex)){
            return false;
        }
        map.put(vertex, new ArrayList<Edge>());
        vertexCount++;
        return true;
    }
    
    /**
     * insert new edge to hashmap if edge doesn't exist already and vertex exists
     * @param edge
     * @return 
     */
    public boolean insertEdge(Edge edge){
        if(!map.containsKey(edge.getFrom()) || !map.containsKey(edge.getTo())){
            return false;
        }
        ArrayList<Edge> edgeList = map.get(edge.getFrom());
        Edge temp = new Edge(edge.getTo(), edge.getFrom());
        ArrayList<Edge> tempList = map.get(temp.getFrom());
        if(edgeList.contains(edge)){
            return false;
        }
        edgeList.add(edge);
        map.put(edge.getFrom(), edgeList);
        edgeCount++;
        if(!isDirected){
            tempList.add(temp);
            map.put(temp.getFrom(), tempList);
            edge.getTo().insertOutgoingNeighbors(temp);
            edge.getTo().insertIncomingNeighbors(edge);
        }

        edge.getFrom().insertOutgoingNeighbors(edge);
        edge.getFrom().insertIncomingNeighbors(temp);
        return true;
    }
    
    /**
     * remove vertex, if vertex exists, and removes all edges associated with the vertex
     * @param vertex
     * @return 
     */
    public boolean removeVertex(Vertex vertex){
        if(!map.containsKey(vertex)){
            return false;
        }
        ArrayList<Edge> edgeList = map.get(vertex);
        map.remove(vertex);
        vertexCount--;
        for(Edge edge: edgeList){
            Edge temp = new Edge(edge.getTo(), edge.getFrom());
            map.get(temp.getFrom()).remove(temp);
        }
        return true;
    }
    
    /**
     * remove edge if edge exists
     * @param edge
     * @return 
     */
    public boolean removeEdge(Edge edge){
        ArrayList<Edge> edgeList = map.get(edge.getFrom());
        if(edgeList.remove(edge)){
            map.put(edge.getFrom(), edgeList);
            edgeCount--;
            Edge temp = new Edge(edge.getTo(), edge.getFrom());
            ArrayList<Edge> tempList = map.get(temp.getFrom());
            if(!isDirected){
                tempList.remove(temp);
                map.put(temp.getFrom(), tempList);
                edge.getTo().removeOutgoingNeighbors(temp);
                edge.getTo().removeIncomingNeighbors(edge);
            }
            edge.getFrom().removeOutgoingNeighbors(edge);
            edge.getFrom().removeIncomingNeighbors(temp);
            
            return true;
        }
        return false;
    }
    
    /**
     * basic get function.  Gets map
     * @return 
     */
    public HashMap getMap(){
        return map;
    }
    
    /**
     * creates a string of BFS order
     * @param vertex
     * @return 
     */
    public String BFS(Vertex vertex){
        StringBuilder str = new StringBuilder();
        ArrayList<Vertex> visited = new ArrayList<>();
        LinkedList<Vertex> queue = new LinkedList<>();
 
        visited.add(vertex);
        queue.add(vertex);
 
        while(!queue.isEmpty())
        {
            vertex = queue.poll();
            str.append(vertex);
            ArrayList<Edge> edges = vertex.getOutgoingNeighbors();
            for(Edge edge: edges){
                if(!visited.contains(edge.getTo())){
                    visited.add(edge.getTo());
                    queue.add(edge.getTo());
                }
            }
        }
        return str.toString();
    }
    
    /**
     * returns String of DFS
     * @param vertex
     * @return 
     */
    public String DFS(Vertex vertex){
        StringBuilder str = new StringBuilder();
        ArrayList<Vertex> visited = new ArrayList<>();
        LinkedList<Vertex> stack = new LinkedList<>();
 
        visited.add(vertex);
        stack.add(vertex);
 
        while(!stack.isEmpty())
        {
            vertex = stack.pop();
            str.append(vertex);
            ArrayList<Edge> edges = vertex.getOutgoingNeighbors();
            for(Edge edge: edges){
                if(!visited.contains(edge.getTo())){
                    visited.add(edge.getTo());
                    stack.push(edge.getTo());
                }
            }
        }
        return str.toString();
    }
    
    public String Dijkstra(Vertex vertex){
        StringBuilder str = new StringBuilder();
        ArrayList<Vertex> notVisited = new ArrayList<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        HashMap<Vertex, Vertex> pred = new HashMap<>();
        HashMap<Vertex, Integer> distances = new HashMap<>();
 
        for(Vertex key: map.keySet()){
            notVisited.add(key);
            pred.put(key, null);
        }
        for(ArrayList<Edge> edges: map.values()){
            for(Edge edge: edges){
                distances.put(edge.getTo(), 0);
            }
        }
        
        queue.add(vertex);
        do{
            vertex = queue.poll();
            notVisited.remove(vertex);
            ArrayList<Vertex> neighbors = vertex.getOutgoingNeighbors();
            for(Vertex neighbor: neighbors){
                if(notVisited.contains(neighbor)){
                    int prev = distances.get(neighbor);
                    distances.put(neighbor, Math.min(distances.get(neighbor), distances.get(vertex) + Cost(vertex, neighbor)));
                    if(prev != distances.get(neighbor)){
                        pred.put(vertex, neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        } while(!notVisited.isEmpty());
        return "didn't have time to implement algorithm that has the shortest path.  Do have most of Dijkstra's completed";
    }
    
    private int Cost(Vertex vertex, Vertex neighbor){
        ArrayList<Edge> neighbors = map.get(vertex);
        for(Edge edge: neighbors){
            if(neighbor.compareTo(edge.getTo()) == 0){
                return edge.getWeight();
            }
        }
        return 0;
    }
    
    public String Prim(Vertex vertex){
        StringBuilder str = new StringBuilder();
        ArrayList<Vertex> notVisited = new ArrayList<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        HashMap<Vertex, Vertex> pred = new HashMap<>();
        HashMap<Vertex, Integer> distances = new HashMap<>();
 
        for(Vertex key: map.keySet()){
            notVisited.add(key);
            pred.put(key, null);
        }
        for(ArrayList<Edge> edges: map.values()){
            for(Edge edge: edges){
                distances.put(edge.getTo(), 0);
            }
        }
        
        queue.add(vertex);
        do{
            vertex = queue.poll();
            notVisited.remove(vertex);
            ArrayList<Vertex> neighbors = vertex.getOutgoingNeighbors();
            for(Vertex neighbor: neighbors){
                if(notVisited.contains(neighbor)){
                    int prev = distances.get(neighbor);
                    distances.put(neighbor, Math.min(distances.get(neighbor), Cost(vertex, neighbor)));
                    if(prev != distances.get(neighbor)){
                        pred.put(vertex, neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        } while(!notVisited.isEmpty());
        return "didn't have time to implement algorithm that has the shortest path.  Do have most of Dijkstra's completed";
    }

//    public String Topological(){
//        
//    }
    
    /**
     * standard toString method
     * @return 
     */
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        map.entrySet().forEach((entry) -> {
            str.append(entry.getKey()).append(entry.getValue()).append("\n");
        });
        return str.toString();
    }
}