
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class FitFrame extends Frame {

    private final double[] drift_;

    public FitFrame( Frame in ) {
        super( "fit", in.getGrid() );
        Grid grid = getGrid();

        // Fit each trace/retrace section to a straight line.
        // It's assumed small enough in time that it's a good approximation.
        // Since the trace/retrace are of the same surface, we can
        // cancel out the real signal, and leave just noise+drift.
        int nx = grid.nx();
        int ny = grid.ny();
        int ny2 = grid.ny() * 2;
        int nx2 = grid.nx() * 2;
        Fit[] xfits = new Fit[ ny2 ];
        for ( int jy = 0; jy < ny2; jy++ ) {
            double[] xdeltas = new double[ nx ];
            for ( int ix = 0; ix < nx; ix++ ) {
                int ks0 = jy * nx2 + nx + ix;
                int ks1 = jy * nx2 + nx - 1 - ix;
                xdeltas[ ix ] = 0.5 * ( in.getSample( ks0 )
                                      - in.getSample( ks1 ) );
            }

            // Cubic fit might be better here.
            xfits[ jy ] = createLinearFit( xdeltas );
        }

        // Calculate linear sections per grid point.
        // This is missing an unknown offset for each (trace/retrace) section.
        int ns = grid.sampleCount();
        double[] xdrift = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            int jy = is / nx2;
            int jx = is % nx2;
            xdrift[ is ] = xfits[ jy ].f( jx - nx );
        }

        // Now try to calculate the longer range drift.
        // We are not now assuming that the drift is linear over this range.
        double[] ydrift = new double[ ns ];
        boolean fitYDrift = false;
        if ( fitYDrift ) {

            // If you have a good model for how deltaZ varies non-linearly
            // as a function of time over timescales like that of the
            // Y trace/retrace, I think you can use it here to fit
            // longer-scale offsets.
            // But if there's no good model, that won't help, because the only
            // reliable input data we have is deltaZ(t), and you can't recover
            // Z(t) from it unambiguously.
            double[] ydeltas = new double[ ny ];
            for ( int iy = 0; iy < ny; iy++ ) {
                double s = 0;
                int ky0 = ny - 1 - iy;
                int ky1 = ny + iy;
                for ( int jx = 0; jx < nx2; jx++ ) {
                    int is0 = ky0 * nx2 + jx;
                    int is1 = ky1 * nx2 + jx;
                    SamplePos sp0 = grid.samplePos( is0 );
                    SamplePos sp1 = grid.samplePos( is1 );
                    assert sp0.ix == sp1.ix;
                    assert sp0.iy == sp1.iy;
                    assert sp1.phase - sp0.phase == 2;
                    double delta = ( in.getSample( is1 ) - xdrift[ is1 ] )
                                 - ( in.getSample( is0 ) - xdrift[ is0 ] );
                    s += delta;
                }
                double meanDelta = s / nx2;
                ydeltas[ iy ] = 0.5 * meanDelta;
            }
            Fit yfit = createDeltaFit( ydeltas );
            for ( int is = 0; is < ns; is++ ) {
                double y = ( ( 2.0 * is - ns ) / ns ) * ny;  // -ny..+ny
                ydrift[ is ] = yfit.f( y );
            }
        }
        else {

            // Calculate the offsets by dead reckoning - just look
            // at the start and finish of each linear sub-fit to work
            // out the offset for the next one.
            // This risks getting lost the more sections it's done for.
            double[] yoffs = new double[ ny2 ];
            for ( int jy = 1; jy < ny2; jy++ ) {
                Fit fit = xfits[ jy ];
                yoffs[ jy ] = yoffs[ jy - 1 ] + fit.f( nx ) - fit.f( -nx );
            }
            for ( int is = 0; is < ns; is++ ) {
                int jy = is / nx2;
                ydrift[ is ] = yoffs[ jy ];
            }
        }

        // Combine the offsets with the line sections to get the drift.
        drift_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            drift_[ is ] = xdrift[ is ] + ydrift[ is ];
        }
    }

    public double getSample( int is ) {
        return drift_[ is ];
    }

    /**
     * The assumption is that the data array is symmetric the array index
     * is proportional to x (so that the first elements are near zero).
     */
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

    /**
     * The input is an array of deltaZ values: deltas[i] = z(i)-z(-i)
     * (so that the first elements are near zero).  The output is a
     * function giving z(i).  I can't see how to do that in general,
     * though if you have an accurate analytic model it might be possible.
     * Or maybe it's possible using some Fourier magic.
     *
     * <p>Not currently implemented.
     */
    private Fit createDeltaFit( double[] deltas ) {
        throw new UnsupportedOperationException();
    }

    public static void main( String[] args ) throws IOException {
        Grid grid = new Grid( 100, 100 );
        final SynthFrame synth =
            new SynthFrame( "z", grid, new Random( 234555L ) );
        final Frame surface = synth.getSurface();
        final Frame inDrift = synth.getDrift();
        final FitFrame outDrift = new FitFrame( synth );
        final Frame out = new Frame( "out", grid ) {
            public double getSample( int is ) {
                return synth.getSample( is ) - outDrift.getSample( is );
            }
        };
        Frame zdiff = new Frame( "zdiff", grid ) {
            public double getSample( int is ) {
                return out.getSample( is ) - surface.getSample( is );
            }
        };
        Frame driftDiff = new Frame ( "drdiff", grid ) {
            public double getSample( int is ) {
                return inDrift.getSample( is ) - outDrift.getSample( is );
            }
        };
        Util.writeFrames( new Frame[] {
            synth,
            surface,
            inDrift,
            outDrift,
            out,
            zdiff,
            driftDiff,
        } );
    }
}
