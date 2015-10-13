
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class Drifter {

    private final Grid grid_;
    private final double[] drift_;

    public Drifter( Frame frm ) {
        grid_ = frm.getGrid();

        // Fit each trace/retrace section to a straight line.
        // It's assumed small enough in time that it's a good approximation.
        // Since the trace/retrace are of the same surface, we can
        // cancel out the real signal, and leave just noise+drift.
        int nx = grid_.nx();
        int ny = grid_.ny();
        int ny2 = grid_.ny() * 2;
        int nx2 = grid_.nx() * 2;
        Fit[] xfits = new Fit[ ny2 ];
        for ( int jy = 0; jy < ny2; jy++ ) {
            double[] xdeltas = new double[ nx ];
            for ( int ix = 0; ix < nx; ix++ ) {
                int kx0 = jy * nx2 + nx + ix;
                int kx1 = jy * nx2 + nx - 1 - ix;
                xdeltas[ ix ] = 0.5 * ( frm.getSample( kx0 )
                                      - frm.getSample( kx1 ) );
            }

            // Cubic fit might be better here.
            xfits[ jy ] = createLinearFit( xdeltas );
        }

        // Calculate linear sections per grid point.
        // This is missing an unknown offset for each (trace/retrace) section.
        int ns = grid_.sampleCount();
        double[] adrift = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            int jy = is / nx2;
            int jx = is % nx2;
            adrift[ is ] = xfits[ jy ].f( jx - nx );
        }

        // Calculate the offsets by dead reckoning - just look
        // at the start and finish to work out the offset for the next one.
        // This risks getting lost the more sections it's done for.
        double[] yoffs = new double[ ny2 ];
        for ( int jy = 1; jy < ny2; jy++ ) {
            Fit fit = xfits[ jy ];
            yoffs[ jy ] = yoffs[ jy - 1 ] + fit.f( nx ) - fit.f( -nx );
        }

        // Combine the offsets with the line sections to get the drift.
        drift_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            int jy = is / nx2;
            drift_[ is ] = adrift[ is ] + yoffs[ jy ];
        }
    }

    // You can work out the the offset residuals and try to use that to
    // correct dead reckoning errors if there is a retrace in Y as well as X.
    // However, fitting is a bit complicated, since you have the difference
    // of two functions as a function of delta t, rather than the value
    // of the function itself.  It is possible to work back from there to
    // fit a known/assumed functional form, but may be difficult analytically.
    //  double[] ydeltas = new double[ ny2 ];
    //  for ( int iy = 0; iy < ny; iy++ ) {
    //      int ky0 = iy;
    //      int ky1 = ny2 - 1 - iy;
    //      double s = 0;
    //      for ( int jx = 0; jx < nx2; jx++ ) {
    //          int is0 = ky0 * nx2 + jx;
    //          int is1 = ky1 * nx2 + jx;
    //          SamplePos sp0 = grid_.samplePos( is0 );
    //          SamplePos sp1 = grid_.samplePos( is1 );
    //          assert sp0.ix == sp1.ix;
    //          assert sp0.iy == sp1.iy;
    //          assert sp1.phase - sp0.phase == 2;
    //          double delta =
    //               ( samples[ is1 ] - adrift[ is1 ] - yoffs[ ky1 ] )
    //             - ( samples[ is0 ] - adrift[ is0 ] - yoffs[ ky0 ] );
    //          s += delta;
    //      }
    //      double meanDelta = s / nx2;
    //      ydeltas[ iy ] = 0.5 * meanDelta;
    //  }
    //  Fit deltaFit = createLinearFit( ydeltas );

    public Frame getDrift() {
        return new Frame( "drift", grid_ ) {
            public double getSample( int is ) {
                return drift_[ is ];
            }
        };
    }

    private Fit createLinearFit( double[] data ) {
        int n = data.length;
        double sxy = 0;
        double sxx = 0;
        for ( int i = 0; i < n; i++ ) {
            sxy += data[ i ] * i;
            sxx += i * i;
        }
        final double m = sxy / sxx;
        return new Fit() {
            public double f( double x ) {
                return m * x;
            }
        };
    }

    public static void main( String[] args ) throws IOException {
        Grid grid = new Grid( 100, 100 );
        final Frame in = new SynthFrame( "z", grid, new Random( 234555L ) );
        Drifter drifter = new Drifter( in );
        final Frame drift = drifter.getDrift();
        Frame sum = new Frame( "out", grid ) {
            public double getSample( int is ) {
                return in.getSample( is ) - drift.getSample( is );
            }
        };
        Util.writeFrames( new Frame[] { in, drift, sum } );
    }
}
