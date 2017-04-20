package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

// TODO: change draw methods so that only the graphics object is changed if possible
// TODO: revise all constructors of Drawable, change variables to be changed through member methods if possible
public interface Drawable {
	public void draw( Graphics2D g );

	public boolean contains( int x, int y );
	
	public BasicStroke getStroke();

	public void setStroke( BasicStroke stroke );
	
	public Color getLineColor();

	public void setLineColor( Color lineColor );

	public Color getFillColor();
	
	public void setFillColor( Color fillColor );

	public double getAngle();

	public void setAngle( double angle );

	public Point2D getTranslation();

	public void setTranslation( Point2D translation );

	public Point2D getScale();

	public void setScale( Point2D scale );

	public double getMinX();

	public double getMinY();

	public double getMaxX();

	public double getMaxY();

	public Point2D getCenter();

	public Point2D getSize();

	public Rectangle2D getBounds();
}
