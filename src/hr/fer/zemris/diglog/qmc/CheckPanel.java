package hr.fer.zemris.diglog.qmc;

import hr.fer.zemris.diglog.qmc.watcher.StringWatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by bmihaela.
 */
public class CheckPanel extends JPanel {
	private java.util.List<String> options;
	FontMetrics fm = getFontMetrics(getFont());
	private int maxStringWidth;
	private static final int CHECK_BOX_SIZE = 16;
	private static final int XMARGIN = 4;
	private static final int YMARGIN = 4;
	private static final int XTEXT_BOX_MARGIN = 3;
	private static final int OPTION_MARGIN = 6;
	private boolean firstRun = true;
	private java.util.List<StringWatcher> watchers;
	private String selected;
	private static final Font CHECK_FONT = new Font("Sans", Font.BOLD, 20);


	public CheckPanel(String checkOption, String... checkOptions) {
		options = new ArrayList<>();
		options.add(checkOption);
		maxStringWidth = fm.stringWidth(checkOption);
		for (String option : checkOptions) {
			int curretnWidth = fm.stringWidth(option);
			if (curretnWidth > maxStringWidth) {
				maxStringWidth = curretnWidth;
			}
			options.add(option);
		}

		selected = " ";
		watchers = new ArrayList<>();

		MouseAdapter clickCheck = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for (StringWatcher stringWatcher : watchers) {
					if (stringWatcher.contains(e.getPoint().getX(), e.getPoint().getY())) {
						selected = stringWatcher.getElement();
						break;
					}
				}
				revalidate();
				repaint();
			}
		};
		this.addMouseListener(clickCheck);
	}

	public String getSelected(){
		return selected;
	}

	public int getIndex(){
		for(int i = 0; i < options.size(); i++){
			if(options.get(i).equals(selected)){
				return i+1;
			}
		}
		return 0;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Dimension dimension = this.getSize();
		int begX = XMARGIN;
		int begY = (int) (dimension.getHeight() / 2 - YMARGIN / 2 - CHECK_BOX_SIZE
				/ 2);

		int lastWidth = 0;
		int i = 0;

		for (String option : options) {
			g.setFont(QMCApp.DEFAULT_FONT);
			g.setColor(Color.WHITE);
			g.fillRect(begX + lastWidth + i * (CHECK_BOX_SIZE + XTEXT_BOX_MARGIN + OPTION_MARGIN), begY,
					CHECK_BOX_SIZE, CHECK_BOX_SIZE);
			g.setColor(Color.BLACK);
			g.drawRect(begX + lastWidth + i * (CHECK_BOX_SIZE + XTEXT_BOX_MARGIN + OPTION_MARGIN), begY,
					CHECK_BOX_SIZE, CHECK_BOX_SIZE);
			g.drawString(option, begX + lastWidth + i * (CHECK_BOX_SIZE + XTEXT_BOX_MARGIN + OPTION_MARGIN) +
					CHECK_BOX_SIZE + XTEXT_BOX_MARGIN, begY + CHECK_BOX_SIZE - (CHECK_BOX_SIZE / 2 - fm.getHeight() /
					2));

			if (selected.equals(option)) {
				g.setFont(CHECK_FONT);
				g.drawString("\u2718", begX + lastWidth + i * (CHECK_BOX_SIZE + XTEXT_BOX_MARGIN + OPTION_MARGIN),
						begY + CHECK_BOX_SIZE);
			}

			if (firstRun) {
				watchers.add(new StringWatcher(begX + lastWidth + i * (CHECK_BOX_SIZE + XTEXT_BOX_MARGIN +
						OPTION_MARGIN), begY,
						CHECK_BOX_SIZE + XTEXT_BOX_MARGIN + fm.stringWidth(option), CHECK_BOX_SIZE, option));
			}
			lastWidth += fm.stringWidth(option);
			i++;
		}
		firstRun = false;
	}
}
