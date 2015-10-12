
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Util {

    private Util() {
    }

    public static Frame createFrame( final Grid grid, final double[] samples ) {
        return new Frame() {
            public Grid getGrid() {
                return grid;
            }
            public double[] getSamples() {
                return samples;
            }
        };
    }

    public static void writeFrame( Frame frame ) throws IOException {
        String pixName = "pixels.csv";
        String sampName = "samples.csv";
        Grid g = frame.getGrid();
        System.err.println( pixName + ": " + g.pixelCount() + " rows" );
        OutputStream pixOut = new FileOutputStream( pixName );
        writePixels( frame, pixOut );
        pixOut.close();
        System.err.println( sampName + ": " + g.sampleCount() + " rows" );
        OutputStream sampOut = new FileOutputStream( sampName );
        writeSamples( frame, sampOut );
        sampOut.close();
    }

    public static void writeSamples( Frame frame, OutputStream out )
            throws IOException {
        Grid grid = frame.getGrid();
        double[] samples = frame.getSamples();
        PrintStream pout = new PrintStream( new BufferedOutputStream( out ) );
        pout.println( "t,ix,iy,phase,z" );
        int ns = samples.length;
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = grid.samplePos( is );
            pout.println( is + ","
                        + spos.ix + ","
                        + spos.iy + ","
                        + spos.phase + "," 
                        + samples[ is ] );
        }
        pout.flush();
    }

    public static void writePixels( Frame frame, OutputStream out )
            throws IOException {
        Grid grid = frame.getGrid();
        double[] samples = frame.getSamples();
        PrintStream pout = new PrintStream( new BufferedOutputStream( out ) );
        int np = grid.pixelCount();
        double[] sqs = new double[ 4 ];
        pout.println( "t,ix,iy,s0,s1,s2,s3" );
        for ( int ip = 0; ip < np; ip++ ) {
            PixelPos ppos = grid.pixelPos( ip );
            for ( short iq = 0; iq < 4; iq++ ) {
                SamplePos spos = new SamplePos( ppos.ix, ppos.iy, iq );
                sqs[ iq ] = samples[ grid.sampleIndex( spos ) ];
            }
            pout.println( ip + ","
                        + ppos.ix + ","
                        + ppos.iy + ","
                        + sqs[ 0 ] + ","
                        + sqs[ 1 ] + ","
                        + sqs[ 2 ] + ","
                        + sqs[ 3 ] );
        }
        pout.flush();
    }
}
