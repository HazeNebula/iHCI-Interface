package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Ellipse implements Drawable {
	private Shape ellipse;
	private double x1, y1, x2, y2, angle;
	private Point2D translation;
	private Point2D scale;
	private Color lineColor;
	private Color fillColor;
	private BasicStroke stroke;

	public Ellipse( double x1, double y1, double x2, double y2, Color lineColor, Color fillColor, BasicStroke stroke ) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.angle = 0.0d;
		this.translation = new Point2D.Double();
		this.scale = new Point2D.Double( 1.0d, 1.0d );
		this.ellipse = null;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.stroke = stroke;
	}

	@Override
	public void draw( Graphics2D g ) {
		Ellipse2D e = new Ellipse2D.Double( getMinX(), getMinY(), getWidth(), getHeight() );

		AffineTransform t = AffineTransform.getTranslateInstance( getCenterX(), getCenterY() );
		t.rotate( angle );
		t.scale( scale.getX(), scale.getY() );
		t.translate( -getCenterX(), -getCenterY() );
		ellipse = t.createTransformedShape( e );

		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();

		g.setColor( fillColor );		
		g.transform( t );		
		g.fillOval( (int)getMinX(), (int)getMinY(), (int)getWidth(), (int)getHeight() );

		AffineTransform inverseT;
		try {
			inverseT = t.createInverse();
		} catch ( NoninvertibleTransformException exception ) {
			inverseT = new AffineTransform();
			inverseT.setToIdentity();
			
			System.err.println( exception.getMessage() );
		}
		g.transform( inverseT );

		g.setStroke( stroke );
		g.setColor( lineColor );
		g.draw( ellipse );
		
		g.setColor( oldColor );
		g.setStroke( oldStroke );
	}

	@Override
	public boolean contains( int x, int y ) {
		return ellipse.contains( x, y );
	}
	
	@Override
	public boolean intersects( Rectangle2D rect ) {
		return ellipse.intersects( rect );
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
		this.stroke = stroke;
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
		x1 -= this.translation.getX();
		y1 -= this.translation.getY();
		x2 -= this.translation.getX();
		y2 -= this.translation.getY();
		
		this.translation = translation;
		
		x1 += this.translation.getX();
		y1 += this.translation.getY();
		x2 += this.translation.getX();
		y2 += this.translation.getY();
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
		return Math.min( x1, x2 );
	}

	@Override
	public double getMinY() {
		return Math.min( y1, y2 );
	}

	@Override
	public double getMaxX() {
		return Math.max( x1, x2 );
	}

	@Override
	public double getMaxY() {
		return Math.max( y1, y2 );
	}

	private double getWidth() {
		return Math.abs( x1 - x2 );
	}

	private double getHeight() {
		return Math.abs( y1 - y2 );
	}

	private double getCenterX() {
		return getMinX() + getWidth() / 2;
	}

	private double getCenterY() {
		return getMinY() + getHeight() / 2;
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
		return ellipse.getBounds2D();
	}
}
