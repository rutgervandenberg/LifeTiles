package nl.tudelft.lifetiles.graph.model;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import nl.tudelft.lifetiles.core.util.Logging;
import nl.tudelft.lifetiles.core.util.Settings;
import nl.tudelft.lifetiles.core.util.Timer;
import nl.tudelft.lifetiles.graph.traverser.EmptySegmentTraverser;
import nl.tudelft.lifetiles.graph.traverser.MutationIndicationTraverser;
import nl.tudelft.lifetiles.graph.traverser.ReferencePositionTraverser;
import nl.tudelft.lifetiles.graph.traverser.UnifiedPositionTraverser;
import nl.tudelft.lifetiles.sequence.model.Sequence;
import nl.tudelft.lifetiles.sequence.model.SequenceSegment;

/**
 * The Tile holds the graph and will be transformed to this modelgraph so
 * that the graph can be drawn on the screen.
 *
 */
public class GraphContainer {

    /**
     * The setting key for empty segments.
     */
    private static final String SETTING_EMPTY = "empty_segments";

    /**
     * The setting key for mutation indication.
     */
    private static final String SETTING_MUTATION = "mutations";

    /**
     * The Current graph that this model is holding.
     */
    private Graph<SequenceSegment> graph;

    /**
     * The Current graph that this model is holding in bucket cache form.
     */
    private final BucketCache segmentBuckets;

    /**
     * The set of currently visible sequencesegments.
     */
    private Set<SequenceSegment> visibles;

    /**
     * The set of visible sequences.
     */
    private Set<Sequence> visibleSequences;

    /**
     * The amount of vertices to be placed in one bucket.
     */
    private static final int NUM_VERTICES_BUCKET = Integer.parseInt(Settings
            .get("num_vertices_bucket"));

    /**
     * create a new Tile.
     *
     * @param graph
     *            The initial graph
     * @param reference
     *            Reference currently active in the graph controller.
     */
    public GraphContainer(final Graph<SequenceSegment> graph,
            final Sequence reference) {
        this.graph = graph;
        for (SequenceSegment segment : this.graph.getAllVertices()) {
            segment.setReferenceStart(1);
            segment.setReferenceEnd(Long.MAX_VALUE);
            segment.setMutation(null);
        }
        alignGraph();
        if (reference != null) {
            findMutations(reference);
        }
        segmentBuckets = new BucketCache(graph.getAllVertices().size()
                / NUM_VERTICES_BUCKET, this.graph);
        visibles = graph.getAllVertices();

    }

    /**
     * Align the graph.
     */
    private void alignGraph() {

        UnifiedPositionTraverser.unifyGraph(graph);

        if (Settings.getBoolean(SETTING_EMPTY)) {
            EmptySegmentTraverser.addEmptySegmentsGraph(graph);
        }

    }

    /**
     * Find the mutations on the graph.
     *
     * @param reference
     *            Reference of the graph which is used to indicate mutations.
     */
    private void findMutations(final Sequence reference) {

        if (!Settings.getBoolean(SETTING_MUTATION)) {
            return;
        }
        ReferencePositionTraverser.referenceMapGraph(graph, reference);
        MutationIndicationTraverser.indicateGraphMutations(graph, reference);

    }

    /**
     * Change the graph by selecting the sequences to draw.
     *
     * @param visibleSequences
     *            the sequences to display
     */
    public final void setVisible(final Set<Sequence> visibleSequences) {
        Timer timer = Timer.getAndStart();
        // Find out which vertices are visible now
        Set<SequenceSegment> vertices = new TreeSet<SequenceSegment>();

        for (SequenceSegment segment: graph.getAllVertices()) {
            //copy the set of sequences because retainAll modifies the original set
            Set<Sequence> intersect;
            intersect = new HashSet<Sequence>(segment.getSources());
            //check if any of the visible sequences are in this nodes sources
            intersect.retainAll(visibleSequences);
            if (!intersect.isEmpty()) {
                vertices.add(segment);
            }
        }

        visibles = vertices;
        this.visibleSequences = visibleSequences;
        timer.stopAndLog("Creating visible graph");
    }

    /**
     * Get the visible segments that this model is holding.
     *
     * @param start
     *            starting bucket position
     * @param end
     *            the last bucket position
     * @return graph
     */
    public final Set<SequenceSegment> getVisibleSegments(final int start,
            final int end) {

        Set<SequenceSegment> copy = new TreeSet<SequenceSegment>();
        for (SequenceSegment seg : segmentBuckets.getSegments(start, end)) {
            try {
                copy.add(seg.clone());
            } catch (CloneNotSupportedException e) {
                Logging.exception(e);
            }
        }

        // Keep only the sequencesegments that are visible
        copy.retainAll(visibles);

        // Set the sources so they only contain the visible sequences
        if (visibleSequences != null) {
            for (SequenceSegment vertex : copy) {
                vertex.getSources().retainAll(visibleSequences);
            }
        }

        return copy;
    }

    /**
     * Returns the bucketCache to check the current position.
     *
     * @return the bucketCache of the graph.
     */
    public final BucketCache getBucketCache() {
        return segmentBuckets;
    }

}
