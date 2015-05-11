package nl.tudelft.lifetiles.tilegraph;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import nl.tudelft.lifetiles.graph.Graph;
import nl.tudelft.lifetiles.graph.sequence.SequenceSegment;
import nl.tudelft.lifetiles.graph.sequence.mutation.DeletionMutation;
import nl.tudelft.lifetiles.graph.sequence.mutation.InsertionMutation;
import nl.tudelft.lifetiles.graph.sequence.mutation.Mutation;
import nl.tudelft.lifetiles.graph.sequence.mutation.PolymorphismMutation;

/**
 * The TileView is responsible for displaying the graph given from
 * the TileController.
 *
 */
@SuppressWarnings("restriction")
public class TileView {
	/**
	 * The root contains all the to be displayed
	 * elements.
	 */
	private Group root;
	/**
	 * The nodes contains all Vertices to be displayed.
	 */
	private Group nodes;
	/**
	 * The edges contains all EdgeLines to be displayed.
	 */
	private Group edges;

	/**
	 * The Controller that controls this view.
	 */
	private TileController controller;

	/**
	 * Add the controller to this view.
	 *
	 * @param tvc
	 *            - the controller
	 */
	public final void addController(final TileController tvc) {
		controller = tvc;
	}

	/**
	 * Create the TileView by intilializing the groups where the to be drawn
	 * vertices and edges are stored.
	 */
	public TileView() {
		root = new Group();
		nodes = new Group();
		edges = new Group();
	}

	/**
	 * Draw the given graph.
	 * @param gr
	 *            - Graph to be drawn
	 * @return - the elements that must be displayed on the screen
	 */
	public final Group drawGraph(final Graph<SequenceSegment> gr) {
		List<Long> lanes = new LinkedList<Long>();
		PriorityQueue<SequenceSegment> it = sortStartVar(gr);
		while (!it.isEmpty()) {
			SequenceSegment segment = it.poll();
			checkAvailable(segment, lanes);
		}
		root.getChildren().addAll(edges, nodes);
		return root;
	}

	/**
	 * This will sort the nodes based on the
	 * starting position.
	 * Beware: temporary code which will be obsolete with #56 Internal
	 * sorting of edges on destination starting position
	 * @param gr
	 *            - the graph that contains the to be sorted nodes
	 * @return - Iterator of the sorted list
	 */
	@Deprecated
	private PriorityQueue<SequenceSegment> sortStartVar(final Graph<SequenceSegment> gr) {
		PriorityQueue<SequenceSegment> it = new PriorityQueue<SequenceSegment>();
		for (SequenceSegment segment : gr.getAllVertices()) {
			it.add(segment);
		}
		return it;
	}

	/**
	 * Functions that will find if it can draw a segment
	 * at a certain position.
	 * @param segment
	 *            - segment to be drawn
	 * @param lanes
	 *            - already drawn segments
	 */
	private void checkAvailable(final SequenceSegment segment, final List<Long> lanes) {
		for (int i = 0; i <= lanes.size(); i++) {
			if (i >= lanes.size() || lanes.get(i) <= segment.getStart() && segmentFree(i, segment, lanes)) {
				segmentInsert(i, segment, lanes);
				String text = segment.getContent().toString();
				drawVertex(text, segment.getStart(), i, segment.getContent().length(), segment.getSources().size(), sequenceColor(segment.getMutation()));
				break;
			}
		}
	}

	/**
	 * Returns the mutation color of a given mutation. Default if no mutation.
	 * @param mutation
	 *            - mutation to return color from.
	 * @return color of the mutation
	 */
	private Color sequenceColor(final Mutation mutation) {
		if (mutation instanceof DeletionMutation) {
			return Color.web("f35959");
		} else if (mutation instanceof InsertionMutation) {
			return Color.web("8df08c");
		} else if (mutation instanceof PolymorphismMutation) {
			return Color.web("4091ff");
		}
		return Color.web("a1d3ff");
	}

	/**
	 * Check if there is a free spot to draw the segment at this location.
	 * @param i
	 *            - location in the linked list of already drawn segments
	 * @param segment
	 *            - segment to be drawn
	 * @param lanes
	 *            - already drawn segments
	 * @return - Boolean indicating if there is a free spot
	 */
	private Boolean segmentFree(final int i, final SequenceSegment segment, final List<Long> lanes) {
		for (int w = 0; w < segment.getSources().size(); w++) {
			if ((i + w <= lanes.size()) && (lanes.get(i + w) > segment.getStart())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Insert a segment in the linked list.
	 * @param i
	 *            - location in the linked list of already drawn segments
	 * @param segment
	 *            - segment to be inserted
	 * @param lanes
	 *            - linked list
	 */
	private void segmentInsert(final int i, final SequenceSegment segment, final List<Long> lanes) {
		for (int w = 0; w < segment.getSources().size(); w++) {
			if (i + w < lanes.size()) {
				lanes.set(i + w, segment.getEnd());
			} else {
				lanes.add(i + w, segment.getEnd());
			}
		}
	}

	/**
	 * Create a Vertex that can be displayed on the screen.
	 * @param text
	 *            - text of the dna segment
	 * @param x
	 *            - top left x coordinate
	 * @param y
	 *            - top left y coordinate
	 * @param height
	 *            - the height of the vertex
	 * @param color
	 *            - the colour of the vertex
	 */
	private void drawVertex(final String text, final double x, final double y,
			final double width, final double height, final Color color) {
		Vertex v = new Vertex(text, x, y, width, height, color);
		nodes.getChildren().add(v);
	}

	/**
	 * Change Vertex colour.
	 * @param v
	 *            - vertex to be changed.
	 * @param color
	 *            - the new colour
	 */
	public final void changeVertexColour(final Vertex v, final Color color) {
		v.setColour(color);
	}

}
