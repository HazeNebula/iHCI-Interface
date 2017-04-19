package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Triangle implements Drawable {
	private Polygon triangle;
	private Point2D[] points;
	private double angle;
	private Point2D translation;
	private Point2D scale;
	private Color lineColor;
	private Color fillColor;
	private BasicStroke stroke;

	public Triangle( double x1, double y1, double x2, double y2, Color lineColor, Color fillColor, BasicStroke stroke ) {
		this.points = new Point2D[3];
		this.points[0] = new Point2D.Double( ( x1 + x2 ) / 2.0d, y1 );
		this.points[1] = new Point2D.Double( x1, y2 );
		this.points[2] = new Point2D.Double( x2, y2 );
		this.angle = 0.0d;
		this.translation = new Point2D.Double();
		this.scale = new Point2D.Double( 1.0d, 1.0d );
		this.triangle = null;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.stroke = stroke;
	}

	@Override
	public void draw( Graphics2D g ) {
		Point2D center = getCenter();
		Point2D[] destPoints = new Point2D[3];

		AffineTransform t = AffineTransform.getTranslateInstance( center.getX(), center.getY() );
		t.rotate( angle );
		t.scale( scale.getX(), scale.getY() );
		t.translate( -center.getX(), -center.getY() );
		t.transform( points, 0, destPoints, 0, points.length );

		triangle = new Polygon();
		for ( Point2D p : destPoints ) {
			triangle.addPoint( (int)p.getX(), (int)p.getY() );
		}

		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();

		g.setColor( fillColor );
		g.fillPolygon( triangle );
		g.setStroke( stroke );
		g.setColor( lineColor );
		g.drawPolygon( triangle );

		g.setColor( oldColor );
		g.setStroke( oldStroke );
	}

	@Override
	public boolean contains( int x, int y ) {
		return triangle.contains( x, y );
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
	public float getStrokeWidth() {
		return stroke.getLineWidth();
	}

	@Override
	public void setStrokeWidth( float strokeWidth ) {
		this.stroke = new BasicStroke( strokeWidth );
	}

	@Override
	public double getAngle() {
		return angle;
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
		points[2].setLocation( points[2].getX() - this.translation.getX(), points[2].getY() - this.translation.getY() );
		
		this.translation = translation;
		
		points[0].setLocation( points[0].getX() + this.translation.getX(), points[0].getY() + this.translation.getY() );
		points[1].setLocation( points[1].getX() + this.translation.getX(), points[1].getY() + this.translation.getY() );
		points[2].setLocation( points[2].getX() + this.translation.getX(), points[2].getY() + this.translation.getY() );
	}

	@Override
	public Point2D getScale() {
		return scale;
	}

	@Override
	public void setScale( Point2D scale ) {
		this.scale = scale;
	}

	@Override
	public double getMinX() {
		return points[1].getX();
	}

	@Override
	public double getMinY() {
		return points[0].getY();
	}

	@Override
	public double getMaxX() {
		return points[2].getX();
	}

	@Override
	public double getMaxY() {
		return points[1].getY();
	}

	private double getWidth() {
		return getMaxX() - getMinX();
	}

	private double getHeight() {
		return getMaxY() - getMinY();
	}

	@Override
	public Point2D getCenter() {
		return new Point2D.Double( ( points[0].getX() + points[1].getX() + points[2].getX() ) / 3.0d, ( points[0].getY() + points[1].getY() + points[2].getY() ) / 3.0d );
	}

	@Override
	public Point2D getSize() {
		return new Point2D.Double( getWidth(), getHeight() );
	}

	@Override
	public Rectangle2D getBounds() {
		return triangle.getBounds2D();
	}
}
