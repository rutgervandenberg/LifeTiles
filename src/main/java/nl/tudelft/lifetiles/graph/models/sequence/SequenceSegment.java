package nl.tudelft.lifetiles.graph.models.sequence;

import java.util.Iterator;
import java.util.Set;

import nl.tudelft.lifetiles.graph.view.Mutation;

/**
 * @author Rutger van den Berg Contains a partial sequence.
 */
public class SequenceSegment implements Comparable<SequenceSegment> {
    /**
     * The content of this segment.
     */
    private SegmentContent contentVar;
    /**
     * Contains the sources containing this segment.
     */
    private Set<Sequence> sourcesVar;

    /**
     * The start position for this segment.
     */
    private long startVar;
    /**
     * The end position for this segment.
     */
    private long endVar;
    /**
     * The unified start position for this segment.
     */
    private long unifiedStartVar = 1;
    /**
     * The unified end position for this segment.
     */
    private long unifiedEndVar = Long.MAX_VALUE;
    /**
     * The start position in comparison with the reference.
     */
    private long referenceStartVar = 1;
    /**
     * The end position in comparison with the reference.
     */
    private long referenceEndVar = Long.MAX_VALUE;

    /**
     * The mutation annotation of this segment.
     */
    private Mutation mutationVar;

    /**
     * @param sources
     *            The sources containing this segment.
     * @param startPosition
     *            The start position for this segment.
     * @param endPosition
     *            The end position for this segment.
     * @param content
     *            The content for this segment.
     */
    public SequenceSegment(final Set<Sequence> sources,
            final long startPosition, final long endPosition,
            final SegmentContent content) {
        sourcesVar = sources;
        startVar = startPosition;
        endVar = endPosition;
        contentVar = content;
    }

    /**
     * @return the content
     */
    public final SegmentContent getContent() {
        return contentVar;
    }

    /**
     * @return the end position
     */
    public final long getEnd() {
        return endVar;
    }

    /**
     * @return the sources
     */
    public final Set<Sequence> getSources() {
        return sourcesVar;
    }

    /**
     * @return the start position
     */
    public final long getStart() {
        return startVar;
    }

    /**
     * @return the unified start position
     */
    public final long getUnifiedStart() {
        return unifiedStartVar;
    }

    /**
     * @param unifiedStart
     *            unified start position of this sequence segment.
     */
    public final void setUnifiedStart(final long unifiedStart) {
        unifiedStartVar = unifiedStart;
    }

    /**
     * @return the unified end position
     */
    public final long getUnifiedEnd() {
        return unifiedEndVar;
    }

    /**
     * @param unifiedEnd
     *            unified end position of this sequence segment.
     */
    public final void setUnifiedEnd(final long unifiedEnd) {
        unifiedEndVar = unifiedEnd;
    }

    /**
     * @return mutation annotation of sequence segment.
     */
    public final Mutation getMutation() {
        return mutationVar;
    }

    /**
     * @param mutation
     *            Mutation which is annotated onto the sequence segment.
     */
    public final void setMutation(final Mutation mutation) {
        mutationVar = mutation;
    }

    /**
     * Returns the distance between this sequence segment and another.
     * (non-euclidian distance)
     *
     * @param other
     *            Sequence segment which needs to be compared.
     * @return
     *         Distance between this sequence and other sequence.
     */
    public final long distanceTo(final SequenceSegment other) {
        return other.getUnifiedStart() - getUnifiedEnd() - 1;
    }

    /**
     * Compares two segments, first by start positions, then end positions, then
     * content, then sources.
     *
     * @param other
     *            Sequence segment which needs to be compared.
     * @return the compare value of the start positions.
     */
    @Override
    public final int compareTo(final SequenceSegment other) {
        int candidateComp = Long.compare(this.getUnifiedStart(),
                other.getUnifiedStart());
        if (candidateComp == 0) {
            candidateComp = Long.compare(this.getStart(), other.getStart());
        }
        if (candidateComp == 0) {
            candidateComp = Long.compare(this.getUnifiedEnd(),
                    other.getUnifiedEnd());
        }
        if (candidateComp == 0) {
            candidateComp = Long.compare(this.getEnd(), other.getEnd());
        }
        if (candidateComp == 0) {
            candidateComp = this.getContent().toString()
                    .compareTo(other.getContent().toString());
        }
        if (candidateComp == 0) {
            candidateComp = this.getSources().size()
                    - other.getSources().size();
        }
        if (candidateComp == 0) {
            Iterator<Sequence> thisIt = this.getSources().iterator();
            Iterator<Sequence> otherIt = other.getSources().iterator();
            while (thisIt.hasNext()) {
                if (candidateComp == 0) {
                    candidateComp = thisIt.next().getIdentifier()
                            .compareTo(otherIt.next().getIdentifier());
                }
            }
        }
        return candidateComp;
    }

    /**
     * Returns the reference start position.
     *
     * @return reference start position.
     */
    public final long getReferenceStart() {
        return referenceStartVar;
    }

    /**
     * Returns the reference end position.
     *
     * @return reference end position.
     */
    public final long getReferenceEnd() {
        return referenceEndVar;
    }

    /**
     * Sets the reference start position.
     *
     * @param referenceStart
     *            Reference start position.
     */
    public final void setReferenceStart(final long referenceStart) {
        referenceStartVar = referenceStart;
    }

    /**
     * Sets the reference end position.
     *
     * @param referenceEnd
     *            Reference end position.
     */
    public final void setReferenceEnd(final long referenceEnd) {
        referenceEndVar = referenceEnd;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        result += (int) (unifiedEndVar ^ (unifiedEndVar >>> prime + 1));
        result = prime * result
                + (int) (unifiedStartVar ^ (unifiedStartVar >>> prime + 1));
        result = prime * result;
        if (contentVar != null) {
            result += contentVar.hashCode();
        }
        result = prime * result + (int) (endVar ^ (endVar >>> prime + 1));
        result = prime * result;
        if (mutationVar != null) {
            result += mutationVar.hashCode();
        }
        result = prime * result;
        if (sourcesVar != null) {
            result += sourcesVar.hashCode();
        }
        result = prime * result + (int) (startVar ^ (startVar >>> prime + 1));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SequenceSegment)) {
            return false;
        }
        SequenceSegment other = (SequenceSegment) obj;
        if (unifiedEndVar != other.getUnifiedEnd()) {
            return false;
        }
        if (unifiedStartVar != other.getUnifiedStart()) {
            return false;
        }
        if (contentVar == null) {
            if (other.contentVar != null) {
                return false;
            }
        } else if (!contentVar.equals(other.contentVar)) {
            return false;
        }
        if (endVar != other.endVar) {
            return false;
        }
        if (sourcesVar == null) {
            if (other.sourcesVar != null) {
                return false;
            }
        } else if (!sourcesVar.equals(other.sourcesVar)) {
            return false;
        }
        if (startVar != other.startVar) {
            return false;
        }
        return true;
    }
}
