
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Util {

    private Util() {
    }

    public static void writeFrames( Frame[] frames ) throws IOException {
        String pixName = "pixels.csv";
        String sampName = "samples.csv";
        Grid g = frames[ 0 ].getGrid();
        System.err.println( pixName + ": " + g.pixelCount() + " rows" );
        OutputStream pixOut = new FileOutputStream( pixName );
        writePixels( frames, pixOut );
        pixOut.close();
        System.err.println( sampName + ": " + g.sampleCount() + " rows" );
        OutputStream sampOut = new FileOutputStream( sampName );
        writeSamples( frames, sampOut );
        sampOut.close();
    }

    public static void writeSamples( Frame[] frames, OutputStream out )
            throws IOException {
        Grid grid = frames[ 0 ].getGrid();
        PrintStream pout = new PrintStream( new BufferedOutputStream( out ) );
        StringBuffer hdr = new StringBuffer( "t,ix,iy,phase" );
        for ( Frame frm : frames ) {
            hdr.append( "," )
               .append( frm.getName() );
        }
        pout.println( hdr.toString() );
        int ns = grid.sampleCount();
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = grid.samplePos( is );
            StringBuffer sbuf = new StringBuffer();
            sbuf.append( is )
                .append( "," )
                .append( spos.ix )
                .append( "," )
                .append( spos.iy )
                .append( "," )
                .append( spos.phase );
            for ( Frame frm : frames ) {
                sbuf.append( "," )
                    .append( frm.getSample( is ) );
            }
            pout.println( sbuf.toString() );
        }
        pout.flush();
    }

    public static void writePixels( Frame[] frames, OutputStream out )
            throws IOException {
        Grid grid = frames[ 0 ].getGrid();
        PrintStream pout = new PrintStream( new BufferedOutputStream( out ) );
        StringBuffer hdr = new StringBuffer( "ip,ix,iy" );
        for ( Frame frm : frames ) {
            for ( int iq = 0; iq < 4; iq++ ) {
                hdr.append( "," )
                   .append( frm.getName() )
                   .append( iq );
            }
        }
        pout.println( hdr.toString() );
        int np = grid.pixelCount();
        double[] sqs = new double[ 4 ];
        for ( int ip = 0; ip < np; ip++ ) {
            PixelPos ppos = grid.pixelPos( ip );
            int ix = ppos.ix;
            int iy = ppos.iy;
            StringBuffer sbuf = new StringBuffer();
            sbuf.append( ip )
                .append( "," )
                .append( ix )
                .append( "," )
                .append( iy );
            for ( Frame frm : frames ) {
                for ( short iq = 0; iq < 4; iq++ ) {
                    int is = grid.sampleIndex( new SamplePos( ix, iy, iq ) );
                    sbuf.append( "," )
                        .append( frm.getSample( is ) );
                }
            }
            pout.println( sbuf.toString() );
        }
        pout.flush();
    }
}
