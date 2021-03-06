package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Selection {
	private static final double GAP_SIZE = 10.0d;

	private Drawable shape;
	private int numButtons;
	private double x1, y1, x2, y2;
	private BasicStroke stroke;

	public Selection( Drawable shape, int numButtons ) {
		this.shape = shape;
		this.numButtons = numButtons;

		Rectangle2D bounds = shape.getBounds();
		double lineWidth = shape.getStroke().getLineWidth();

		x1 = bounds.getMinX() - GAP_SIZE - lineWidth / 1.5d;
		y1 = bounds.getMinY() - GAP_SIZE - lineWidth / 1.5d;
		x2 = bounds.getMaxX() + GAP_SIZE + lineWidth / 1.5d;
		y2 = bounds.getMaxY() + GAP_SIZE + lineWidth / 1.5d;

		stroke = new BasicStroke( 3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 2.0f }, 0.0f );
	}

	public void draw( Graphics2D g ) {
		Rectangle2D bounds = shape.getBounds();
		double lineWidth = shape.getStroke().getLineWidth();

		x1 = bounds.getMinX() - GAP_SIZE - lineWidth / 1.5d;
		y1 = bounds.getMinY() - GAP_SIZE - lineWidth / 1.5d;
		x2 = bounds.getMaxX() + GAP_SIZE + lineWidth / 1.5d;
		y2 = bounds.getMaxY() + GAP_SIZE + lineWidth / 1.5d;

		Stroke oldStroke = g.getStroke();
		g.setStroke( stroke );
		g.draw( new Rectangle2D.Double( x1, y1, x2 - x1, y2 - y1 ) );
		g.setStroke( oldStroke );
	}

	public Point2D getButtonSpace( Dimension canvas, int buttonSize, int buttonGapSizeSmall, int buttonGapSizeBig ) {
		Point2D buttonCoords = new Point2D.Double();

		int widthNeeded = buttonGapSizeSmall + buttonSize;
		if ( widthNeeded < ( canvas.width - (int)x2 ) ) {
			buttonCoords.setLocation( x2 + buttonGapSizeSmall, 0.0d );
		} else if ( widthNeeded < canvas.width ) {
			buttonCoords.setLocation( x1 - widthNeeded, 0.0d );
		} else {
			return null;
		}
		buttonCoords.setLocation( Math.max( Math.min( buttonCoords.getX(), canvas.width - widthNeeded ), buttonGapSizeSmall ), 0.0d );

		int heightNeeded = numButtons * ( buttonSize + buttonGapSizeSmall ) + buttonGapSizeBig;
		if ( heightNeeded < ( canvas.height - (int)y1 ) ) {
			buttonCoords.setLocation( buttonCoords.getX(), y1 );
		} else if ( heightNeeded < canvas.height ) {
			buttonCoords.setLocation( buttonCoords.getX(), canvas.height - heightNeeded );
		} else {
			return null;
		}
		buttonCoords.setLocation( buttonCoords.getX(), Math.max( Math.min( buttonCoords.getY(), canvas.height ), buttonGapSizeSmall ) );

		return buttonCoords;
	}

	public Drawable getShape() {
		return shape;
	}
}
