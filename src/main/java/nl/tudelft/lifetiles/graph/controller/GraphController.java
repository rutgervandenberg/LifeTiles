package nl.tudelft.lifetiles.graph.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import nl.tudelft.lifetiles.core.controller.AbstractController;
import nl.tudelft.lifetiles.core.controller.MenuController;
import nl.tudelft.lifetiles.core.util.Message;
import nl.tudelft.lifetiles.graph.model.DefaultGraphParser;
import nl.tudelft.lifetiles.graph.model.FactoryProducer;
import nl.tudelft.lifetiles.graph.model.Graph;
import nl.tudelft.lifetiles.graph.model.GraphContainer;
import nl.tudelft.lifetiles.graph.model.GraphFactory;
import nl.tudelft.lifetiles.graph.model.GraphParser;
import nl.tudelft.lifetiles.graph.view.TileView;
import nl.tudelft.lifetiles.graph.view.VertexView;
import nl.tudelft.lifetiles.notification.controller.NotificationController;
import nl.tudelft.lifetiles.notification.model.NotificationFactory;
import nl.tudelft.lifetiles.sequence.model.Sequence;
import nl.tudelft.lifetiles.sequence.model.SequenceSegment;

/**
 * The controller of the graph view.
 *
 * @author Joren Hammudoglu
 * @author AC Langerak
 *
 */
public class GraphController extends AbstractController {

    /**
     * The wrapper element.
     */
    @FXML
    private ScrollPane wrapper;

    /**
     * The model of the graph.
     */
    private GraphContainer model;

    /**
     * The view of the graph.
     */
    private TileView view;

    /**
     * The view controller.
     */
    private Graph<SequenceSegment> graph;

    /**
     * Graph node used to draw the update graph based on bucket cache technique.
     */
    private Group graphNode;

    /**
     * currentPosition of the view in the scrollPane, should only redraw if
     * position has changed.
     */
    private int currentPosition = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void initialize(final URL location,
            final ResourceBundle resources) {

        NotificationFactory notFact = new NotificationFactory();

        listen(Message.OPENED, (controller, args) -> {
            assert (controller instanceof MenuController);
            assert (args[0] instanceof File && args[1] instanceof File);
            try {
                loadGraph((File) args[0], (File) args[1]);
            } catch (Exception exception) {

                exception.printStackTrace();
                shout(NotificationController.NOTIFY, notFact
                        .getNotification(exception));
            }
        });

        listen(Message.FILTERED, (controller, args) -> {
            assert (args.length == 1);
            if (!(args[0] instanceof Set<?>)) {
                throw new IllegalArgumentException(
                        "Argument not of type Set<Sequence>");
            }

            model.setVisible((Set<Sequence>) args[0]);
            repaint(true);
        });

    }

    /**
     *
     * @return the currently loaded graph.
     */
    public final Graph<SequenceSegment> getGraph() {
        if (graph == null) {
            throw new IllegalStateException("Graph not loaded.");
        }
        return graph;
    }

    /**
     * Load a new graph from the specified file.
     *
     *
     * @param vertexfile
     *            The file to get vertices for.
     * @param edgefile
     *            The file to get edges for.
     * @throws IOException
     *             When an IO error occurs while reading one of the files.
     */
    private void loadGraph(final File vertexfile, final File edgefile)
            throws IOException {
        // create the graph
        GraphFactory<SequenceSegment> factory = FactoryProducer.getFactory();
        GraphParser parser = new DefaultGraphParser();
        graph = parser.parseGraph(vertexfile, edgefile, factory);

        shout(Message.LOADED, graph, parser.getSequences());
        repaint(false);

    }

    /**
     * Repaints the view.
     */
    private void repaint(Boolean force) {
        if (graph != null) {
            if (model == null) {
                model = new GraphContainer(graph);
            }
            view = new TileView(this);

            Group root = new Group();

            wrapper.hvalueProperty().addListener(
                    (observable, oldValue, newValue) -> {

                        Group root2 = new Group();
                        repaintPosition(root, newValue.doubleValue());
                    });

            Rectangle clip = new Rectangle(getMaxUnifiedEnd(graph)
                    * VertexView.HORIZONTALSCALE, 0);
            root.getChildren().add(clip);

            repaintPosition(root, wrapper.hvalueProperty().doubleValue());
        }
    }

    /**
     * Repaints the view indicated by the bucket in the given position.
     *
     * @param root
     *            Root group used to store the visualized graph in.
     * @param position
     *            Position in the scrollPane.
     */
    private void repaintPosition(final Group root, final double position) {
        int nextPosition = getBucketPosition(position);
        if (currentPosition != nextPosition || model.isChanged()) {
            if (graphNode != null) {
                graphNode.getChildren().clear();
            }
            graphNode = new Group();
            graphNode.getChildren().add(drawGraph(nextPosition));
            root.getChildren().add(graphNode);
            wrapper.setContent(root);
            currentPosition = nextPosition;

        }
    }

    /**
     * Get the maximal unified end position based on the sinks of the graph.
     *
     * @param graph
     *            Graph for which the width must be calculated.
     * @return the maximal unified end position.
     */
    private long getMaxUnifiedEnd(final Graph<SequenceSegment> graph) {
        long max = 0;
        for (SequenceSegment vertex : graph.getSinks()) {
            if (max < vertex.getUnifiedEnd()) {
                max = vertex.getUnifiedEnd();
            }
        }
        return max;
    }

    /**
     * Return the position in the bucket.
     *
     * @param position
     *            Position in the scrollPane.
     * @return position in the bucket.
     */
    private final int getBucketPosition(final double position) {
        return model.getBucketCache().bucketPercentagePosition(position);
    }

    /**
     * Creates a drawable object of the graph from the model.
     *
     * @param position
     *            Bucket position of the scrollPane.
     * @return Group object to be drawn on the screen
     */
    public final Group drawGraph(final int position) {
        return view.drawGraph(model.getSegments(position), graph);
    }

    /**
     * Set that this segment is selected and set those sequences visible.
     *
     * @param segment
     *            The selected segment
     */
    // Removed final for testing, cannot use PowerMockito because of current bug
    // with javafx 8
    public void clicked(final SequenceSegment segment) {
        shout(Message.FILTERED, segment.getSources());
    }

    /**
     * Set that this segment is hovered over.
     *
     * @param segment
     *            the hovered element
     * @param hovering
     *            set if mouse is entering this segment or leaving
     */
    // Removed final for testing, cannot use PowerMockito because of current bug
    // with javafx 8
    public void hovered(final SequenceSegment segment, final Boolean hovering) {
        // TODO: Message to say that a segment is hovered over

    }

}
