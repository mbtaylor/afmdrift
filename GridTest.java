
public class GridTest {

    public void testGrid() {
        Gridder gridder = new Gridder( 4, 4 );
        int nx = gridder.nx();
        int ny = gridder.ny();
        int ns = gridder.sampleCount();
        int np = gridder.pixelCount();
        int[] phaseCounts = new int[ 4 ];
        int[] pixCounts = new int[ np ];

        assertEquals( np, nx * ny );
        for ( int ip = 0; ip < np; ip++ ) {
            assertEquals( ip, gridder.pixelIndex( gridder.pixelPos( ip ) ) );
        }

        assertEquals( ns, nx * ny * 4 );
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = gridder.samplePos( is );
            phaseCounts[ spos.phase_ ]++;
            PixelPos ppos = new PixelPos( spos.ix_, spos.iy_ );
            pixCounts[ gridder.pixelIndex( ppos ) ]++;
            assertEquals( is, gridder.sampleIndex( spos ) );
        }
        for ( int iq = 0; iq < 4; iq++ ) {
            assertEquals( np, phaseCounts[ iq ] );
        }
        for ( int ip = 0; ip < np; ip++ ) {
            assertEquals( 4, pixCounts[ ip ] );
        }
    }

    public static void assertEquals( int i1, int i2 ) {
        if ( i1 != i2 ) {
            throw new AssertionError( i1 + " != " + i2 );
        }
    }

    public static void main( String[] args ) {
        new GridTest().testGrid();
    }
}
