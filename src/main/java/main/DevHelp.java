package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;

import font.DynamicFont;
import panel.CompareTab;
import panel.ConvertTab;
import panel.FormatTab;
import panel.RequestTab;

public class DevHelp extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTabbedPane tab;
	private FormatTab formatTab;
	private CompareTab compareTab;
	private RequestTab requestTab;
	private ConvertTab convertTab;
	
	public DevHelp( ) {
		setVisible( true );
		setTitle( "DevHelp" );
		setSize( 1800, 960 );
		setFont( DynamicFont.getFont( "NotoSansKhmer-Regular").deriveFont( 4f ) );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch ( UnsupportedLookAndFeelException e ) {
		} 
		setLayout( new BorderLayout() );
		Font notoFont = DynamicFont.getFont("NotoSansKhmer-Regular").deriveFont(13f);
		setFontRecursively(this, notoFont);
		tab = new JTabbedPane();
		tab.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular").deriveFont( 14f ) );
		
		
		formatTab = new FormatTab();
		compareTab = new CompareTab();
		requestTab = new RequestTab();
		convertTab = new ConvertTab();
		tab.addTab( "សម្រួលទ្រង់ទ្រាយ (Ctrl + F)", formatTab );
		tab.addTab( "ប្រៀបធៀប (Ctrl + Enter)", compareTab );		
		tab.addTab( "បម្លែង", convertTab );		
		tab.addTab( "សំណើរ", requestTab );
		add(tab);
		tab.setBackground(UIManager.getColor("TabbedPane.background"));
		tab.setOpaque(true);
		setVisible( true );
	
	}
	
	public void setFontRecursively(Component component, Font font) {
		component.setFont(font);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				setFontRecursively(child, font);
			}
		}
	}
	
	public static void main( String[] args ) {
			new DevHelp();
		
	}
}
