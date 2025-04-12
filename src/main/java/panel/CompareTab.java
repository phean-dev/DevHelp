package panel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.undo.UndoManager;

import font.DynamicFont;

public class CompareTab extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private UndoManager undoManager;
	
	public CompareTab() {
		setLayout( new GridLayout( 1, 2, 5, 5 ) );
		undoManager = new UndoManager();
		JTextArea textArea1 = new JTextArea( 20, 40 );
		JTextArea textArea2 = new JTextArea( 20, 40 );
		textArea1.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular").deriveFont( 13f ) );
		textArea2.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular").deriveFont( 13f ) );
		JScrollPane scrollPane1 = new JScrollPane( textArea1 );
		scrollPane1.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		scrollPane1.getVerticalScrollBar().setUnitIncrement( 16 );
		JScrollPane scrollPane2 = new JScrollPane( textArea2 );
		scrollPane2.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		scrollPane2.getVerticalScrollBar().setUnitIncrement( 16 );
		scrollPane1.getVerticalScrollBar().addAdjustmentListener( new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged( AdjustmentEvent e ) {
				if ( !e.getValueIsAdjusting() ) {
					scrollPane2.getVerticalScrollBar().setValue( e.getValue() );
				}
			}
		} );
		scrollPane2.getVerticalScrollBar().addAdjustmentListener( new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged( AdjustmentEvent e ) {
				if ( !e.getValueIsAdjusting() ) {
					scrollPane1.getVerticalScrollBar().setValue( e.getValue() );
				}
			}
		} );
		scrollPane1.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged( AdjustmentEvent e ) {
				if ( !e.getValueIsAdjusting() ) {
					scrollPane2.getHorizontalScrollBar().setValue( e.getValue() );
				}
			}
		} );
		scrollPane2.getHorizontalScrollBar().addAdjustmentListener( new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged( AdjustmentEvent e ) {
				if ( !e.getValueIsAdjusting() ) {
					scrollPane1.getHorizontalScrollBar().setValue( e.getValue() );
				}
			}
		} );
		textArea1.addKeyListener( new KeyAdapter() {
			
			public void keyPressed( KeyEvent e ) {
				if ( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER ) {
					compareTextAreas( textArea1, textArea2 );
				}
			};
		} );
		textArea2.addKeyListener( new KeyAdapter() {
			
			public void keyPressed( KeyEvent e ) {
				if ( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER ) {
					compareTextAreas( textArea1, textArea2 );
				}
			};
		} );
		setDoOrUndo(textArea1);
		setDoOrUndo(textArea2);
		add( scrollPane1 );
		add( scrollPane2 );
	}
	
	private void setDoOrUndo(JTextArea textArea1) {
		textArea1.getDocument().addUndoableEditListener(undoManager);

		textArea1.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control Z"), "Undo");
		textArea1.getActionMap().put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
		textArea1.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("control Y"), "Redo");
		textArea1.getActionMap().put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
	}
	private static void compareTextAreas( JTextArea textArea1, JTextArea textArea2 ) {
		String[] lines1 = textArea1.getText().split( "\n" );
		String[] lines2 = textArea2.getText().split( "\n" );
		Highlighter highlighter1 = textArea1.getHighlighter();
		Highlighter highlighter2 = textArea2.getHighlighter();
		highlighter1.removeAllHighlights();
		highlighter2.removeAllHighlights();
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter( Color.gray );
		int maxLines = Math.max( lines1.length, lines2.length );
		for ( int i = 0; i < maxLines; i++ ) {
			String line1 = i < lines1.length ? lines1[i] : "";
			String line2 = i < lines2.length ? lines2[i] : "";
			if ( !line1.equals( line2 ) ) {
				try {
					int start1 = textArea1.getLineStartOffset( i );
					int end1 = textArea1.getLineEndOffset( i );
					highlighter1.addHighlight( start1, end1, painter );
					int start2 = textArea2.getLineStartOffset( i );
					int end2 = textArea2.getLineEndOffset( i );
					highlighter2.addHighlight( start2, end2, painter );
				} catch ( BadLocationException e ) {
					e.printStackTrace();
				}
			}
		}
	}
}
