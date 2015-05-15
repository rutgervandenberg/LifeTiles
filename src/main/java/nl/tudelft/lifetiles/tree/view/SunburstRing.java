package nl.tudelft.lifetiles.tree.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import nl.tudelft.lifetiles.tree.model.PhylogeneticTreeItem;

/**
 *
 * @author Albert Smit
 *
 */

public class SunburstRing extends SunburstUnit {

    /**
     * Creates a SunburstRing.
     *
     *
     * @param v
     *            the {@link PhylogeneticTreeItem} this part of the ring will
     *            represent
     * @param layer
     *            the layer at which it is located in the tree, layer 0 is the
     *            first layer
     * @param degreeStart
     *            the start position in degrees
     * @param degreeEnd
     *            the end position in degrees
     * @param centerX
     *            the X coordinate of the center of the circle
     * @param centerY
     *            the Y coordinate of the center of the circle
     */
    public SunburstRing(final PhylogeneticTreeItem v, final int layer,
            final double degreeStart, final double degreeEnd,
            final double centerX, final double centerY) {
        // set the value, and create the text and semi-circle
        value = v;
        name = new Text(value.getName());
        display = createRing(layer, degreeStart, degreeEnd, centerX, centerY);

        // calculate the positon of the text
        double radius = CENTER_RADIUS + (layer * RING_WIDTH) + (RING_WIDTH / 2);

        double degreeCenter = degreeStart
                + SunburstUnit.calculateAngle(degreeStart, degreeEnd) / 2;

        double angle = degreeCenter * (Math.PI / 180);

        double pointRingCenterX = centerX + radius * Math.sin(angle);
        double pointRingCenterY = centerY - (radius * Math.cos(angle));

        // move the text into position
        name.relocate(pointRingCenterX, pointRingCenterY);

        // add the text and semicircle to the group
        getChildren().addAll(display, name);
    }

    /**
     * Creates a semi-circle with in the specified location.
     *
     * layer starts at 0
     *
     * @param layer
     *            The layer to place this element at, the first child of root is
     *            layer 0.
     * @param degreeStart
     *            the start position in degrees
     * @param degreeEnd
     *            the end position in degrees
     * @param centerX
     *            the X coordinate of the center of the circle
     * @param centerY
     *            the Y coordinate of the center of the circle
     * @return a semi-circle with the specified dimensions
     */
    private Shape createRing(final int layer,
            final double degreeStart, final double degreeEnd,
            final double centerX, final double centerY) {
        Path result = new Path();

        result.setFill(Color.RED);
        result.setFillRule(FillRule.EVEN_ODD);

        // check if this is a large arc
        double arcSize = SunburstUnit.calculateAngle(degreeStart, degreeEnd);
        boolean largeArc = arcSize > 180;

        //calculate the radii of the two arcs
        double innerRadius = CENTER_RADIUS + (layer * RING_WIDTH);
        double outerRadius = innerRadius + RING_WIDTH;

        // convert degrees to radians for Math.sin and Math.cos
        double angleAlpha = degreeStart * (Math.PI / 180);
        double angleAlphaNext = degreeEnd * (Math.PI / 180);

        //calculate the positon of the four corners of the semi-circle
        double point1X = centerX + innerRadius * Math.sin(angleAlpha);
        double point1Y = centerY - (innerRadius * Math.cos(angleAlpha));

        double point2X = centerX + outerRadius * Math.sin(angleAlpha);
        double point2Y = centerY - (outerRadius * Math.cos(angleAlpha));

        double point3X = centerX + outerRadius * Math.sin(angleAlphaNext);
        double point3Y = centerY - (outerRadius * Math.cos(angleAlphaNext));

        double point4X = centerX + innerRadius * Math.sin(angleAlphaNext);
        double point4Y = centerY - (innerRadius * Math.cos(angleAlphaNext));

        // draw the semi-circle
        MoveTo move1 = new MoveTo(point1X, point1Y);
        LineTo line12 = new LineTo(point2X, point2Y);

        ArcTo arc23 = new ArcTo();
        arc23.setRadiusX(outerRadius);
        arc23.setRadiusY(outerRadius);
        arc23.setX(point3X);
        arc23.setY(point3Y);
        arc23.setSweepFlag(true);
        arc23.setLargeArcFlag(largeArc);

        LineTo line34 = new LineTo(point4X, point4Y);

        ArcTo arc41 = new ArcTo();
        arc41.setRadiusX(innerRadius);
        arc41.setRadiusY(innerRadius);
        arc41.setX(point1X);
        arc41.setY(point1Y);
        arc41.setSweepFlag(false);
        arc41.setLargeArcFlag(largeArc);

        // add all elements to the path
        result.getElements().add(move1);
        result.getElements().add(line12);
        result.getElements().add(arc23);
        result.getElements().add(line34);
        result.getElements().add(arc41);

        return result;
    }

}
