Differences to Draw2d Geometry API


1) only double precision, with imprecise relations


2) scaling is performed relative to the geometries center, not the coordinate system origin

To get the old behavior back, you can use: scale(x, y, new Point());


3) Rectangle contains the right and bottom border


4) Polygon and Polyline instead of PointList