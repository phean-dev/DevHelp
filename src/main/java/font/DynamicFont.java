package font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class DynamicFont {
	
	public static Font getFont( String name) {
		InputStream fontStream =DynamicFont.class.getResourceAsStream( "/fonts/"+name + ".ttf" );
		if (fontStream == null) {
	        System.err.println("Font file not found: /fonts/" + name + ".ttf");
	        return null;
	    }
		Font notoSansKhmer;
		try {
			notoSansKhmer = Font.createFont( Font.TRUETYPE_FONT, fontStream );
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont( notoSansKhmer );
			return notoSansKhmer;
		} catch ( FontFormatException | IOException e ) {
			e.printStackTrace();
			return null;
		}
	}
}
