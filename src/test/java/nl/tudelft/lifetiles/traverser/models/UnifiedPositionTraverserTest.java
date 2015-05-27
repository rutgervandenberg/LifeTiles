package nl.tudelft.lifetiles.traverser.models;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.lifetiles.graph.model.FactoryProducer;
import nl.tudelft.lifetiles.graph.model.Graph;
import nl.tudelft.lifetiles.graph.model.GraphFactory;
import nl.tudelft.lifetiles.graph.model.sequence.DefaultSequence;
import nl.tudelft.lifetiles.graph.model.sequence.SegmentString;
import nl.tudelft.lifetiles.graph.model.sequence.Sequence;
import nl.tudelft.lifetiles.graph.model.sequence.SequenceSegment;
import nl.tudelft.lifetiles.traverser.model.UnifiedPositionTraverser;

public class UnifiedPositionTraverserTest {
    GraphFactory<SequenceSegment> gf;
    static FactoryProducer<SequenceSegment> fp;
    static UnifiedPositionTraverser upt;
    static Set<Sequence> s1;
    SequenceSegment v1, v2;
    Graph<SequenceSegment> gr;

    @BeforeClass
    public static void runOnce() {
        fp = new FactoryProducer<SequenceSegment>();

        Sequence ss1 = new DefaultSequence("reference");

        upt = new UnifiedPositionTraverser();

        s1 = new HashSet<Sequence>();
        s1.add(ss1);
    }

    @Before
    public void setUp() throws Exception {
        gf = fp.getFactory("JGraphT");
        v1 = new SequenceSegment(s1, 1, 11, new SegmentString("AAAAAAAAAA"));
        v2 = new SequenceSegment(s1, 21, 31, new SegmentString("AAAAAAAAAA"));
        gr = gf.getGraph();
        gr.addVertex(v1);
        gr.addVertex(v2);
        gr.addEdge(v1, v2);
    }
    
    @Test
    public void traverseGapTest() {
        upt.unifyGraph(gr);
        assertEquals(1, v1.getUnifiedStart());
        assertEquals(11, v1.getUnifiedEnd());
        assertEquals(11, v2.getUnifiedStart());
        assertEquals(21, v2.getUnifiedEnd());
    }

}