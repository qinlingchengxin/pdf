/*
 * $Id: 0ad64471a2f4ac2c7dc6bb5ae1260055769d4246 $
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 2014-2015 iText Group NV
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 *
 *
 * This class is based on the C# open source freeware library Clipper:
 * http://www.angusj.com/delphi/clipper.php
 * The original classes were distributed under the Boost Software License:
 *
 * Freeware for both open source and commercial applications
 * Copyright 2010-2014 Angus Johnson
 * Boost Software License - Version 1.0 - August 17th, 2003
 *
 * Permission is hereby granted, free of charge, to any person or organization
 * obtaining a copy of the software and accompanying documentation covered by
 * this license (the "Software") to use, reproduce, display, distribute,
 * execute, and transmit the Software, and to prepare derivative works of the
 * Software, and to permit third-parties to whom the Software is furnished to
 * do so, all subject to the following:
 *
 * The copyright notices in the Software and this entire statement, including
 * the above license grant, this restriction and the following disclaimer,
 * must be included in all copies of the Software, in whole or in part, and
 * all derivative works of the Software, unless such copies or derivative
 * works are solely in the form of machine-executable object code generated by
 * a source language processor.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDERS OR ANYONE DISTRIBUTING THE SOFTWARE BE LIABLE
 * FOR ANY DAMAGES OR OTHER LIABILITY, WHETHER IN CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.itextpdf.text.pdf.parser.clipper;

import java.util.Comparator;

public abstract class Point<T extends Number & Comparable<T>> {
    public static class DoublePoint extends Point<Double> {
        public DoublePoint() {
            this( 0, 0 );
        }

        public DoublePoint( double x, double y ) {
            this( x, y, 0 );
        }

        public DoublePoint( double x, double y, double z ) {
            super( x, y, z );
        }

        public DoublePoint( DoublePoint other ) {
            super( other );
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }

    public static class LongPoint extends Point<Long> {
        public static double getDeltaX( LongPoint pt1, LongPoint pt2 ) {
            if (pt1.getY() == pt2.getY()) {
                return Edge.HORIZONTAL;
            }
            else {
                return (double) (pt2.getX() - pt1.getX()) / (pt2.getY() - pt1.getY());
            }
        }

        public LongPoint() {
            this( 0, 0 );
        }

        public LongPoint( long x, long y ) {
            this( x, y, 0 );
        }

        public LongPoint(double x, double y) {
            this((long) x, (long) y);
        }

        public LongPoint( long x, long y, long z ) {
            super( x, y, z );
        }

        public LongPoint( LongPoint other ) {
            super( other );
        }

        public long getX() {
            return x;
        }

        public long getY() {
            return y;
        }

        public long getZ() {
            return z;
        }
    }

    private static class NumberComparator<T extends Number & Comparable<T>> implements Comparator<T> {

        public int compare( T a, T b ) throws ClassCastException {
            return a.compareTo( b );
        }
    }

    static boolean arePointsClose( Point<? extends Number> pt1, Point<? extends Number> pt2, double distSqrd ) {
        final double dx = pt1.x.doubleValue() - pt2.x.doubleValue();
        final double dy = pt1.y.doubleValue() - pt2.y.doubleValue();
        return dx * dx + dy * dy <= distSqrd;
    }

    static double distanceFromLineSqrd( Point<? extends Number> pt, Point<? extends Number> ln1, Point<? extends Number> ln2 ) {
        //The equation of a line in general form (Ax + By + C = 0)
        //given 2 points (x�,y�) & (x�,y�) is ...
        //(y� - y�)x + (x� - x�)y + (y� - y�)x� - (x� - x�)y� = 0
        //A = (y� - y�); B = (x� - x�); C = (y� - y�)x� - (x� - x�)y�
        //perpendicular distance of point (x�,y�) = (Ax� + By� + C)/Sqrt(A� + B�)
        //see http://en.wikipedia.org/wiki/Perpendicular_distance
        final double A = ln1.y.doubleValue() - ln2.y.doubleValue();
        final double B = ln2.x.doubleValue() - ln1.x.doubleValue();
        double C = A * ln1.x.doubleValue() + B * ln1.y.doubleValue();
        C = A * pt.x.doubleValue() + B * pt.y.doubleValue() - C;
        return C * C / (A * A + B * B);
    }

    static DoublePoint getUnitNormal( LongPoint pt1, LongPoint pt2 ) {
        double dx = pt2.x - pt1.x;
        double dy = pt2.y - pt1.y;
        if (dx == 0 && dy == 0) {
            return new DoublePoint();
        }

        final double f = 1 * 1.0 / Math.sqrt( dx * dx + dy * dy );
        dx *= f;
        dy *= f;

        return new DoublePoint( dy, -dx );
    }

    protected static boolean isPt2BetweenPt1AndPt3( LongPoint pt1, LongPoint pt2, LongPoint pt3 ) {
        if (pt1.equals( pt3 ) || pt1.equals( pt2 ) || pt3.equals( pt2 )) {
            return false;
        }
        else if (pt1.x != pt3.x) {
            return pt2.x > pt1.x == pt2.x < pt3.x;
        }
        else {
            return pt2.y > pt1.y == pt2.y < pt3.y;
        }
    }

    protected static boolean slopesEqual( LongPoint pt1, LongPoint pt2, LongPoint pt3 ) {
        return (pt1.y - pt2.y) * (pt2.x - pt3.x) - (pt1.x - pt2.x) * (pt2.y - pt3.y) == 0;
    }

    protected static boolean slopesEqual( LongPoint pt1, LongPoint pt2, LongPoint pt3, LongPoint pt4 ) {
        return (pt1.y - pt2.y) * (pt3.x - pt4.x) - (pt1.x - pt2.x) * (pt3.y - pt4.y) == 0;
    }

    static boolean slopesNearCollinear( LongPoint pt1, LongPoint pt2, LongPoint pt3, double distSqrd ) {
        //this function is more accurate when the point that's GEOMETRICALLY
        //between the other 2 points is the one that's tested for distance.
        //nb: with 'spikes', either pt1 or pt3 is geometrically between the other pts
        if (Math.abs( pt1.x - pt2.x ) > Math.abs( pt1.y - pt2.y )) {
            if (pt1.x > pt2.x == pt1.x < pt3.x) {
                return distanceFromLineSqrd( pt1, pt2, pt3 ) < distSqrd;
            }
            else if (pt2.x > pt1.x == pt2.x < pt3.x) {
                return distanceFromLineSqrd( pt2, pt1, pt3 ) < distSqrd;
            }
            else {
                return distanceFromLineSqrd( pt3, pt1, pt2 ) < distSqrd;
            }
        }
        else {
            if (pt1.y > pt2.y == pt1.y < pt3.y) {
                return distanceFromLineSqrd( pt1, pt2, pt3 ) < distSqrd;
            }
            else if (pt2.y > pt1.y == pt2.y < pt3.y) {
                return distanceFromLineSqrd( pt2, pt1, pt3 ) < distSqrd;
            }
            else {
                return distanceFromLineSqrd( pt3, pt1, pt2 ) < distSqrd;
            }
        }
    }

    private final static NumberComparator NUMBER_COMPARATOR = new NumberComparator();

    protected T x;

    protected T y;

    protected T z;

    protected Point( Point<T> pt ) {
        this( pt.x, pt.y, pt.z );
    }

    protected Point( T x, T y, T z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Point<?>) {
            final Point<?> a = (Point<?>) obj;
            return NUMBER_COMPARATOR.compare( x, a.x ) == 0 && NUMBER_COMPARATOR.compare( y, a.y ) == 0;
        }
        else {
            return false;
        }
    }

    public void set( Point<T> other ) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    public void setX( T x ) {
        this.x = x;
    }

    public void setY( T y ) {
        this.y = y;
    }

    public void setZ( T z ) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + ", z=" + z + "]";
    }

}// end struct IntPoint