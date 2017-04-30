package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Line implements Drawable {
	private static final int HITBOX_SIZE = 25;

	private Line2D line;
	private Point2D[] points;
	private double angle;
	private Point2D translation;
	private Point2D scale;
	private Color lineColor;
	private Color fillColor;
	private BasicStroke stroke;

	public Line( double x1, double y1, double x2, double y2, Color lineColor, Color fillColor, BasicStroke stroke ) {
		points = new Point2D[2];
		points[0] = new Point2D.Double( x1, y1 );
		points[1] = new Point2D.Double( x2, y2 );
		this.angle = 0.0d;
		this.translation = new Point2D.Double();
		this.scale = new Point2D.Double( 1.0d, 1.0d );
		this.line = null;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		setStroke( stroke );
	}

	@Override
	public void draw( Graphics2D g ) {
		AffineTransform t = AffineTransform.getTranslateInstance( getCenterX(), getCenterY() );
		t.rotate( angle );
		t.scale( scale.getX(), scale.getY() );
		t.translate( -getCenterX(), -getCenterY() );
		Point2D[] newPoints = new Point2D[2];
		t.transform( points, 0, newPoints, 0, 2 );
		line = new Line2D.Double( newPoints[0].getX(), newPoints[0].getY(), newPoints[1].getX(), newPoints[1].getY() );

		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();

		g.setStroke( stroke );
		g.setColor( lineColor );
		g.draw( line );

		g.setColor( oldColor );
		g.setStroke( oldStroke );
	}

	@Override
	public boolean contains( int x, int y ) {
		return ( x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY() && line.ptLineDist( (double)x, (double)y ) < HITBOX_SIZE );
	}

	@Override
	public Color getLineColor() {
		return lineColor;
	}
	
	@Override
	public void setLineColor( Color lineColor ) {
		this.lineColor = lineColor;
	}
	
	@Override
	public Color getFillColor() {
		return fillColor;
	}

	@Override
	public void setFillColor( Color fillColor ) {
		this.fillColor = fillColor;
	}

	@Override
	public BasicStroke getStroke() {
		return stroke;
	}
	
	@Override
	public void setStroke( BasicStroke stroke ) {
		this.stroke = new BasicStroke( stroke.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
	}

	@Override
	public double getAngle() {
		return this.angle;
	}

	@Override
	public void setAngle( double angle ) {
		this.angle = angle;
	}

	@Override
	public Point2D getTranslation() {
		return translation;
	}

	@Override
	public void setTranslation( Point2D translation ) {
		points[0].setLocation( points[0].getX() - this.translation.getX(), points[0].getY() - this.translation.getY() );
		points[1].setLocation( points[1].getX() - this.translation.getX(), points[1].getY() - this.translation.getY() );
		
		this.translation = translation;
		
		points[0].setLocation( points[0].getX() + this.translation.getX(), points[0].getY() + this.translation.getY() );
		points[1].setLocation( points[1].getX() + this.translation.getX(), points[1].getY() + this.translation.getY() );
	}

	@Override
	public Point2D getScale() {
		return scale;
	}

	@Override
	public void setScale( Point2D scale ) {
		this.scale = scale;
	}

	public double getX1() {
		return points[0].getX();
	}

	public double getY1() {
		return points[0].getY();
	}

	public double getX2() {
		return points[1].getX();
	}

	public double getY2() {
		return points[1].getY();
	}

	@Override
	public double getMinX() {
		return Math.min( points[0].getX(), points[1].getX() );
	}

	@Override
	public double getMaxX() {
		return Math.max( points[0].getX(), points[1].getX() );
	}

	@Override
	public double getMinY() {
		return Math.min( points[0].getY(), points[1].getY() );
	}

	@Override
	public double getMaxY() {
		return Math.max( points[0].getY(), points[1].getY() );
	}

	private double getWidth() {
		return Math.abs( points[1].getX() - points[0].getX() );
	}

	private double getHeight() {
		return Math.abs( points[1].getY() - points[0].getY() );
	}

	private double getCenterX() {
		return getMinX() + getWidth() / 2.0d;
	}

	private double getCenterY() {
		return getMinY() + getHeight() / 2.0d;
	}

	@Override
	public Point2D getCenter() {
		return new Point2D.Double( getCenterX(), getCenterY() );
	}

	@Override
	public Point2D getSize() {
		return new Point2D.Double( getWidth(), getHeight() );
	}

	@Override
	public Rectangle2D getBounds() {
		return line.getBounds2D();
	}
}
