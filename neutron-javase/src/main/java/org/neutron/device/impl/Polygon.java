package org.neutron.device.impl;
public class Polygon extends Shape {
public int npoints;
public int xpoints[];
public int[] ypoints;

	private Rectangle bounds = new Rectangle();
public Polygon() {
	}
public Polygon(int[] xpoints, int[] ypoints, int npoints) {

		this.xpoints = new int[npoints];
		this.ypoints = new int[npoints];
		this.npoints = npoints;
		//Add a try here to catch failed array copy
		System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
		System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);

		for (int i = 0; i < npoints; i++) {
			bounds.add(xpoints[i], ypoints[i]);
		}
	}
public Polygon(Polygon poly) {
		this(poly.xpoints, poly.ypoints, poly.npoints);
	}
public void addPoint(int x, int y) {
		if (npoints > 0) {
			int xtemp[];
			int ytemp[];
			xtemp = xpoints;
			ytemp = ypoints;
			xpoints = new int[npoints + 1];

			System.arraycopy(xtemp, 0, xpoints, 0, npoints);
			xtemp = null;
			ypoints = new int[npoints + 1];
			System.arraycopy(ytemp, 0, ypoints, 0, npoints);
			ytemp = null;
		} else {
			xpoints = new int[1];
			ypoints = new int[1];
		}
		npoints++;
		xpoints[npoints - 1] = x;//-1 to account for 0 array indexing
		ypoints[npoints - 1] = y;

		bounds.add(x, y);
	}

	public Rectangle getBounds() {
		return bounds;
	}
public boolean contains(int x, int y) {
if (getBounds().contains(x, y)) {

//          Tested and work just fine
			boolean c = false;
			for (int i = 0, j = npoints - 1; i < npoints; j = i++) {
				if ((((ypoints[i] <= y) && (y < ypoints[j])) || ((ypoints[j] <= y) && (y < ypoints[i])))
						&& (x < ((double)(xpoints[j] - xpoints[i]) * (y - ypoints[i])) / (ypoints[j] - ypoints[i]) + xpoints[i])) {
					c = !c;
				}
			}
			return c;
			
//           This code does not work as expected even after fixes for double
//
//			/* Start andys code */
//			int number_of_lines_crossed = 0; // holds lines intersecting with 
//			number_of_lines_crossed = 0; // set lines crossed = 0 for each polygon
//			// lets us know which part start we're looking for	   
//			for (int i = 0; i < npoints - 1; i++) {
//				//GeoPoint A = new GeoPoint();
//				//GeoPoint B = new GeoPoint();        
//				//A.y = ypoints[i];                                       // get y-coordinate
//				//A.x = xpoints[i];                                       // get x-coordinate 
//				//B.y = ypoints[i+1];                                     // get next y-coordinate
//				//B.x = xpoints[i+1];                                     // get next x-coordinate
//
//				if (y != ypoints[i + 1] && (y <= Math.max(ypoints[i], ypoints[i + 1]))
//						&& (y >= Math.min(ypoints[i], ypoints[i + 1])) && ((xpoints[i] >= x) || (xpoints[i + 1] >= x))) { // if polygon contains a suitable value
//					if (((xpoints[i] >= x) && (xpoints[i + 1] >= x))) {
//						number_of_lines_crossed++;//simple case
//					} else {
//						double gradient; //   calc. gradient
//						if (xpoints[i] > xpoints[i + 1]) {
//							gradient = ((double) (xpoints[i] - xpoints[i + 1]) / (double) (ypoints[i] - ypoints[i + 1]));
//						} else {
//							gradient = ((double) (xpoints[i + 1] - xpoints[i]) / (double) (ypoints[i + 1] - ypoints[i]));
//						}
//						double x_intersection_axis = (xpoints[i] - (gradient * ypoints[i])); // calc. intersect with x-axis
//						double x_intersection_line = (gradient * y) + x_intersection_axis; // calc. intersect with y=const
//						//  line extending from location 
//						if ((x_intersection_line <= Math.max(xpoints[i], xpoints[i + 1]))
//								&& // check intersect inside polygon 
//								(x_intersection_line >= Math.min(xpoints[i], xpoints[i + 1]))
//								&& (x_intersection_line >= x)) {
//							number_of_lines_crossed++; // increment line counter
//						} // end check for inside polygon
//					}
//				} // end of if polygon points suitable
//			} // end of run through points in polygon
//
//			if ((number_of_lines_crossed != 0) && // if number of polygon lines crossed
//					(((number_of_lines_crossed % 2) == 1))) { //   by a line in one direction from the
//				return true; //   initial location is odd, the location
//
//				//   lies in that polygon
//
//			} // end of run through polygons

		}
		return false; // return stuffing if things get this far

	} // end of method whichPolygon	   

}