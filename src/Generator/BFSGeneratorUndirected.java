package Generator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.joda.time.LocalDateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import Graph.DirectedEdge;
import Graph.GraphNode;
import Graph.RoadGraph;

public class BFSGeneratorUndirected {
	public static void main(String[] args) throws FileNotFoundException, IOException, XmlPullParserException{
		System.out.println("Run started at"+ LocalDateTime.now() );
		RoadGraph g = new RoadGraph();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput ( new FileReader ("OSMData/NYCOSM.osm"));

		//xpp.setInput ( new FileReader ("OSMData/NYC_sample.osm"));
		g.osmGraphParser(xpp);

		System.out.println("Parsing ended at"+ LocalDateTime.now() );
		LinkedList<GraphNode> nodes = g.nodes;
		LinkedList<DirectedEdge> edges = g.edges;
		GraphNode hub_node = g.LGA_NODE;

		System.out.println("Graph filling started at"+ LocalDateTime.now() );
		//Construct Graph
		SimpleWeightedGraph <GraphNode,DefaultWeightedEdge> gr_t = new  
				SimpleWeightedGraph <GraphNode,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		ListIterator<GraphNode> nodeIterator_t = nodes.listIterator();
		//Adding vertices
		while (nodeIterator_t.hasNext()) {
			GraphNode single_node= nodeIterator_t.next();
			gr_t.addVertex(single_node);
		}
		//adding edges

		ListIterator<DirectedEdge> listIterator_t = edges.listIterator();
		while (listIterator_t.hasNext()) {
			DirectedEdge single_edge = listIterator_t.next();
			float weight = single_edge.getWalkWeight();

			// Loop detection

			if(single_edge.from().getId() == single_edge.to().getId()){
				continue;
			}
			if(weight<=0){
				continue;
			}
			DefaultWeightedEdge temp_e = gr_t.getEdge(single_edge.from(),single_edge.to());
			if(temp_e == null){
				DefaultWeightedEdge e1 = gr_t.addEdge(single_edge.from(),single_edge.to()); 
				gr_t.setEdgeWeight(e1,weight);
			}
		}
		System.out.println("Graph filling ended at"+ LocalDateTime.now() );

		System.out.println("Serialization started at"+ LocalDateTime.now() );

		ObjectOutputStream oos_graph = new ObjectOutputStream(new FileOutputStream("ObjectWarehouse/UnDirectedWalkLimitGraphHashed_v1.obj"));
		oos_graph.writeObject(gr_t);
		oos_graph.close();
	}

}
