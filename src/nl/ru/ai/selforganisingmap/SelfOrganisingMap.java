package nl.ru.ai.selforganisingmap;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SelfOrganisingMap {
	private int numberOfIterations;
	private double startingLearningRate;

	private int mapWidth;
	private double mapRadius;
	private double timeInterval;
	private Random rand;

	private ArrayList<DataVector> data;
	private ArrayList<Node> nodes;

	public SelfOrganisingMap( String mapFile ) {
		readMap( mapFile );
	}

	public SelfOrganisingMap( String dataFile, int numberOfNodes, int numberOfIterations, double startingLearningRate ) {
		this.numberOfIterations = numberOfIterations;
		this.startingLearningRate = startingLearningRate;

		readDataVectors( dataFile );
		this.nodes = new ArrayList<Node>();
		mapWidth = (int)Math.sqrt( numberOfNodes );

		for ( int i = 0; i < numberOfNodes; ++i ) {
			this.nodes.add( new Node( data.get( 0 ).vector.size(), i % mapWidth, i / mapWidth, null ) );
		}

		this.rand = new Random( System.nanoTime() );
		this.mapRadius = (double)mapWidth / 2.0f;
		this.timeInterval = (double)this.numberOfIterations / Math.log( mapRadius );

		this.trainNetwork();
	}

	private void readDataVectors( String fileName ) {
		data = new ArrayList<DataVector>();

		try ( Scanner scanner = new Scanner( new FileInputStream( fileName ) ) ) {
			while ( scanner.hasNext() ) {
				String[] line = scanner.nextLine().split( "," );
				DataVector vector = new DataVector();

				for ( Polygon_t type : Polygon_t.values() ) {
					if ( line[0].equals( type.toString() ) ) {
						vector.classification = type;
						break;
					}
				}

				for ( int i = 1; i < line.length; ++i ) {
					vector.vector.add( Double.parseDouble( line[i] ) );
				}

				vector.normalizeVector();
				data.add( vector );
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	private void readMap( String fileName ) {
		nodes = new ArrayList<Node>();

		try ( Scanner scanner = new Scanner( new FileInputStream( fileName ) ) ) {
			while ( scanner.hasNext() ) {
				String[] line = scanner.nextLine().split( "," );
				Node node = new Node();

				node.x = Integer.parseInt( line[0] );
				node.y = Integer.parseInt( line[1] );

				DataVector vector = new DataVector();

				for ( Polygon_t type : Polygon_t.values() ) {
					if ( line[2].equals( type.toString() ) ) {
						vector.classification = type;
						break;
					}
				}

				for ( int i = 3; i < line.length; ++i ) {
					vector.vector.add( Double.parseDouble( line[i] ) );
				}

				node.weights = vector;
				nodes.add( node );
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		mapWidth = (int)Math.sqrt( nodes.size() );
	}

	public void saveMap( String fileName ) {
		try ( FileWriter writer = new FileWriter( fileName ) ) {
			for ( Node node : nodes ) {
				writer.write( node.toString() + "\n" );
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	private void assignClassification() {
		for ( int i = 0; i < nodes.size(); ++i ) {
			double minDistance = Double.MAX_VALUE;

			for ( int j = 0; j < data.size(); ++j ) {
				double distance = nodes.get( i ).weights.getDistance( data.get( j ) );

				if ( distance < minDistance ) {
					minDistance = distance;
					nodes.get( i ).weights.classification = data.get( j ).classification;
				}
			}
		}
	}

	public void trainNetwork() {
		double learningRate = startingLearningRate;

		for ( int i = 0; i < numberOfIterations; ++i ) {
			int vector = rand.nextInt( data.size() );
			int winningNode = getWinningNode( data.get( vector ) );

			double radius = mapRadius * Math.exp( -(double)i / timeInterval );
			double radiusSq = radius * radius;

			for ( int j = 0; j < nodes.size(); ++j ) {
				double distanceToNodeSq = ( nodes.get( winningNode ).x - nodes.get( j ).x ) * ( nodes.get( winningNode ).x - nodes.get( j ).x ) + ( nodes.get( winningNode ).y - nodes.get( j ).y ) * ( nodes.get( winningNode ).y - nodes.get( j ).y );

				if ( distanceToNodeSq < radiusSq ) {
					double influence = Math.exp( -( distanceToNodeSq ) / ( 2 * radiusSq ) );

					nodes.get( j ).adjustWeights( data.get( vector ), learningRate, influence );
				}
			}

			learningRate = startingLearningRate * Math.exp( -(double)i / timeInterval );
		}

		assignClassification();
	}

	private int getWinningNode( DataVector vector ) {
		double minDistance = Double.MAX_VALUE;
		int index = 0;

		for ( int i = 0; i < nodes.size(); ++i ) {
			double distance = nodes.get( i ).weights.getDistance( vector );

			if ( distance < minDistance ) {
				minDistance = distance;
				index = i;
			}
		}

		return index;
	}

	public Polygon_t getClassification( DataVector vector ) {
		return nodes.get( getWinningNode( vector ) ).weights.classification;
	}
}
