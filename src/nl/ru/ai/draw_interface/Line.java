package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Line implements Drawable {
	private static final int HITBOX_SIZE = 40;

	private Shape line;
	private double x1, y1, x2, y2, angle;
	private Point2D translation;
	private Point2D scale;
	private Color lineColor;
	private Color fillColor;
	private BasicStroke stroke;

	public Line( double x1, double y1, double x2, double y2, Color lineColor, Color fillColor, BasicStroke stroke ) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
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
		Line2D l = new Line2D.Double( x1, y1, x2, y2 );

		AffineTransform t = AffineTransform.getTranslateInstance( getCenterX(), getCenterY() );
		t.rotate( angle );
		t.scale( scale.getX(), scale.getY() );
		t.translate( -getCenterX(), -getCenterY() );
		line = t.createTransformedShape( l );

		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();

		g.setStroke( stroke );
		g.setColor( lineColor );
		g.draw( line );

		g.setColor( oldColor );
		g.setStroke( oldStroke );
	}

	// TODO: fix contains function (using ptLineDist?)
	@Override
	public boolean contains( int x, int y ) {
		return line.intersects( x - HITBOX_SIZE, y - HITBOX_SIZE, HITBOX_SIZE, HITBOX_SIZE );
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

	public double getX1() {
		return x1;
	}

	public double getY1() {
		return y1;
	}

	public double getX2() {
		return x2;
	}

	public double getY2() {
		return y2;
	}

	@Override
	public double getMinX() {
		return Math.min( x1, x2 );
	}

	@Override
	public double getMaxX() {
		return Math.max( x1, x2 );
	}

	@Override
	public double getMinY() {
		return Math.min( y1, y2 );
	}

	@Override
	public double getMaxY() {
		return Math.max( y1, y2 );
	}

	private double getWidth() {
		return Math.abs( x2 - x1 );
	}

	private double getHeight() {
		return Math.abs( y2 - y1 );
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
