package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ActionState {
	private ActionType_t actionType;

	private ArrayList<Drawable> shapes;
	private Drawable shape;
	private Color lineColor;
	private Color fillColor;
	private BasicStroke stroke;
	private Point2D translation;
	private Point2D scale;
	private double angle;
	private String text;

	public ActionState( ActionType_t actionType ) {
		this.actionType = actionType;
		shapes = null;
		lineColor = null;
		fillColor = null;
		stroke = null;
		translation = null;
		scale = null;
		angle = 0.0d;
	}

	public ActionType_t getActionType() {
		return actionType;
	}

	public void setShapes( ArrayList<Drawable> shapes ) {
		this.shapes = shapes;
	}
	
	public ArrayList<Drawable> getShapes() {
		return shapes;
	}
	
	public void setShape( Drawable shape ) {
		this.shape = shape;
	}
	
	public Drawable getShape() {
		return shape;
	}
	
	public void setLineColor( Color lineColor ) {
		this.lineColor = lineColor;
	}

	public Color getLineColor() {
		return lineColor;
	}
	
	public void setFillColor( Color fillColor ) {
		this.fillColor = fillColor;
	}

	public Color getFillColor() {
		return fillColor;
	}
	
	public void setStroke( BasicStroke stroke ) {
		this.stroke = stroke;
	}

	public BasicStroke getStroke() {
		return stroke;
	}
	
	public void setTranslation( Point2D translation ) {
		this.translation = translation;
	}

	public Point2D getTranslation() {
		return translation;
	}
	
	public void setScale( Point2D scale ) {
		this.scale = scale;
	}

	public Point2D getScale() {
		return scale;
	}
	
	public void setAngle( double angle ) {
		this.angle = angle;
	}

	public double getAngle() {
		return angle;
	}
	
	public void setText( String text ) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
}
